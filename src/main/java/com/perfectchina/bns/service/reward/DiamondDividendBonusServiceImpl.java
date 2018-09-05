package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.common.utils.BigDecimalUtil;
import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.AccountPin;
import com.perfectchina.bns.model.AccountPinHistory;
import com.perfectchina.bns.model.reward.DiamondDividendBonusReward;
import com.perfectchina.bns.model.treenode.OpvNetTreeNode;
import com.perfectchina.bns.repositories.AccountPinRepository;
import com.perfectchina.bns.repositories.AccountRepository;
import com.perfectchina.bns.repositories.DiamondDividendBonusRepository;
import com.perfectchina.bns.repositories.OpvNetTreeNodeRepository;
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
public class DiamondDividendBonusServiceImpl implements DiamondDividendBonusService{

    @Autowired
    private AccountPinRepository accountPinRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OpvNetTreeNodeRepository opvNetTreeNodeRepository;
    @Autowired
    private DiamondDividendBonusRepository diamondDividendBonusRepository;

    private BigDecimal totalIntegral = new BigDecimal(0);

    @Override
    public void hasQualification(Long accountId) {
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
            if (Pin.descOf(PinPosition.DIAMOND).getCode() <= Pin.descOf(accountPins.get(i).getPin()).getCode()){
                count+=1;
            }
        }
        if (count >= 6 && contiguousCount == 4){
            BigDecimal integral = getIntegral(count);
            Account account = accountRepository.getAccountById(accountId);
            DiamondDividendBonusReward isExist = diamondDividendBonusRepository.getDiamondDividendBonusRewardByAccount(account);
            if (isExist != null){
                isExist.setAccount(account);
                isExist.setBonusRate(integral);
                isExist.setCreatedBy("TerryTang");
                isExist.setLastUpdatedBy("TerryTang");
                diamondDividendBonusRepository.save(isExist);
            }else {
                DiamondDividendBonusReward diamondDividendBonusReward = new DiamondDividendBonusReward();
                diamondDividendBonusReward.setAccount(account);
                diamondDividendBonusReward.setBonusRate(integral);
                diamondDividendBonusReward.setCreatedBy("TerryTang");
                diamondDividendBonusReward.setLastUpdatedBy("TerryTang");
                diamondDividendBonusRepository.save(diamondDividendBonusReward);
            }
        }
    }

    @Override
    public BigDecimal getIntegral(int count) {
        BigDecimal Integral = BigDecimalUtil.multiply(1.2, Double.valueOf(count));
        totalIntegral = totalIntegral.add(Integral);
        return Integral;
    }

    @Override
    public void calculateTotalIntegral() {
        List<Account> accounts = accountRepository.findAll();
        if (accounts != null){
            for (Account account : accounts){
                hasQualification(account.getId());
            }
        }
    }

    @Override
    public BigDecimal calculateDividendBonus() {
        // 奖金池
        BigDecimal bonusPool = BigDecimalUtil.multiply(0.005D, getTotalOpvs());
        // 每分的奖金值
        BigDecimal avarBonus = bonusPool.divide(totalIntegral,2,BigDecimal.ROUND_DOWN);
        // 根据当天日期获取表中的所有数据
        String currentSnapshotDate = DateUtils.getCurrentSnapshotDate();
        List<DiamondDividendBonusReward> rewards = diamondDividendBonusRepository.getDiamondDividendBonusRewardsByCreationDate(currentSnapshotDate);
        if (rewards != null){
            for (DiamondDividendBonusReward reward : rewards){
                BigDecimal bonusRate = reward.getBonusRate();
                reward.setAmount(avarBonus.multiply(bonusRate));
            }
            batchSave(rewards);
        }
        return null;
    }

    //批量存储的集合
    public void batchSave(List<DiamondDividendBonusReward> rewards) {
        List<DiamondDividendBonusReward> data = new ArrayList<>();
        //批量存储
        for(DiamondDividendBonusReward reward : rewards) {
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

    public static void main(String args[]) {
        BigDecimal multiply = BigDecimalUtil.multiply(0.005D, 20000000000D);
        BigDecimal bigDecimal = new BigDecimal("18000");
        BigDecimal divide = multiply.divide(bigDecimal,2,BigDecimal.ROUND_DOWN);
        String monthOfLastYearSnapshotDate = DateUtils.getMonthOfLastYearSnapshotDate(3);
        String monthOfCurrentYearSnapshotDate = DateUtils.getMonthOfCurrentYearSnapshotDate(2);
        System.out.println(divide);
    }

}
