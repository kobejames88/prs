package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.common.utils.BigDecimalUtil;
import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.AccountPin;
import com.perfectchina.bns.model.reward.DividendBonusReward;
import com.perfectchina.bns.model.treenode.OpvNetTreeNode;
import com.perfectchina.bns.repositories.AccountPinRepository;
import com.perfectchina.bns.repositories.AccountRepository;
import com.perfectchina.bns.repositories.DiamondDividendBonusRepository;
import com.perfectchina.bns.repositories.OpvNetTreeNodeRepository;
import com.perfectchina.bns.service.Enum.DividendBonusType;
import com.perfectchina.bns.service.Enum.Pin;
import com.perfectchina.bns.service.pin.PinPosition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DividendBonusImpl implements DividendBonus{

    @Autowired
    private AccountPinRepository accountPinRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OpvNetTreeNodeRepository opvNetTreeNodeRepository;
    @Autowired
    private DiamondDividendBonusRepository diamondDividendBonusRepository;

    private BigDecimal totalIntegral = new BigDecimal(0);

    // 判断是否拥有资格
    private void hasQualification(Long accountId,String pin) {
        // 获取去年3月到今年2月所有节点的PIN
        Date lastMonth = DateUtils.getMonthOfLastYearDate(3);
        Date currentMonth = DateUtils.getMonthOfCurrentYearDate(2);
        List<AccountPin> accountPins = accountPinRepository.findByAccountIdAndSnapshotDate(accountId,lastMonth,currentMonth);
        int contiguousCount = 1;
        int count = 0;
        // 6个月钻石及以上,并且4个月连续
        for (int i = 0; i < accountPins.size() - 1; ++i) {
            if (!(contiguousCount == 4)){
                if (StringUtils.equals(DateUtils.addMonthAndConvertToSnapShotDate(accountPins.get(i).getCreationDate()),DateUtils.convertToSnapShotDate(accountPins.get(i+1).getCreationDate()))) {
                    contiguousCount++;
                }else {
                    contiguousCount = 1;
                }
            }
            if (Pin.descOf(pin).getCode() <= Pin.descOf(accountPins.get(i).getPin()).getCode()){
                count+=1;
            }
        }
        if (count >= 6 && contiguousCount == 4){
            BigDecimal integral = getIntegral(count);
            Account account = accountRepository.getAccountById(accountId);
            DividendBonusReward isExist = diamondDividendBonusRepository.getDiamondDividendBonusRewardByAccount(account);
            if (isExist != null){
                isExist.setAccount(account);
                isExist.setBonusRate(integral);
                diamondDividendBonusRepository.save(isExist);
            }else {
                DividendBonusReward dividendBonusReward = new DividendBonusReward();
                dividendBonusReward.setAccount(account);
                dividendBonusReward.setBonusRate(integral);
                dividendBonusReward.setCreatedBy("TerryTang");
                dividendBonusReward.setLastUpdatedBy("TerryTang");
                dividendBonusReward.setType(DividendBonusType.descOf(pin).getCode());
                diamondDividendBonusRepository.save(dividendBonusReward);
            }
        }
    }

    // 获取总积分
    private BigDecimal getIntegral(int count) {
        BigDecimal Integral = BigDecimalUtil.multiply(1.2D, Double.valueOf(count));
        totalIntegral = totalIntegral.add(Integral).setScale(2,BigDecimal.ROUND_DOWN);
        return Integral;
    }

    // 计算每人积分和总积分
    private void calculateTotalIntegral(String pin) {
        List<Account> accounts = accountRepository.findAll();
        if (accounts != null){
            for (Account account : accounts){
                hasQualification(account.getId(),pin);
            }
        }
    }

    // 计算分红奖
    private BigDecimal calculateDividendBonus() {
        // 奖金池
        BigDecimal bonusPool = BigDecimalUtil.multiply(0.005D, getTotalOpvs());
        // 每分的奖金值
        BigDecimal avarBonus = bonusPool.divide(totalIntegral,2,BigDecimal.ROUND_DOWN);
        // 根据当天日期获取表中的所有数据
        String currentSnapshotDate = DateUtils.getCurrentSnapshotDate();
        List<DividendBonusReward> rewards = diamondDividendBonusRepository.getDiamondDividendBonusRewardsByCreationDate(currentSnapshotDate);
        if (rewards != null){
            for (DividendBonusReward reward : rewards){
                BigDecimal bonusRate = reward.getBonusRate();
                reward.setAmount(avarBonus.multiply(bonusRate).setScale(2,BigDecimal.ROUND_DOWN));
            }
            batchSave(rewards);
        }
        return null;
    }

    //批量存储的集合
    private void batchSave(List<DividendBonusReward> rewards) {
        List<DividendBonusReward> data = new ArrayList<>();
        //批量存储
        for(DividendBonusReward reward : rewards) {
            if(data.size() == 300) {
                diamondDividendBonusRepository.saveAll(data);
                data.clear();
            }
            data.add(reward);
        }
        if(!data.isEmpty()) {
            diamondDividendBonusRepository.saveAll(data);
        }
    }

    // 获取年度总业绩
    private Double getTotalOpvs(){
        String fromDate = DateUtils.getMonthOfLastYearSnapshotDate(3);
        String toDate = DateUtils.getMonthOfCurrentYearSnapshotDate(2);
        List<OpvNetTreeNode> opvs = opvNetTreeNodeRepository.findBySnapshotDate(fromDate, toDate);
        Double totalOpvs = 0D;
        if (opvs != null){
            for (OpvNetTreeNode opv : opvs){
                totalOpvs += opv.getOpv();
            }
        }
        return totalOpvs;
    }

    @Override
    public void build(String pin) {
        calculateTotalIntegral(pin);
        calculateDividendBonus();
    }

}
