package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.CustomerBonusNet;
import com.perfectchina.bns.model.reward.DistributorBonus;
import com.perfectchina.bns.model.reward.DistributorBonusRate;
import com.perfectchina.bns.model.treenode.FiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.OpvNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.FiveStarNetTreeNodeRepository;
import com.perfectchina.bns.repositories.reward.DistributorBonusRateRepository;
import com.perfectchina.bns.repositories.reward.DistributorBonusRepository;
import com.perfectchina.bns.service.pin.PinPosition;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Stack;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/8
 * @Desc:
 */
@Service
public class DistributorBonusNetServiceImpl implements DistributorBonusNetService {

    @Autowired
    private DistributorBonusRepository distributorBonusRepository;

    @Autowired
    private DistributorBonusRateRepository distributorBonusRateRepository;

    @Autowired
    private FiveStarNetTreeNodeRepository fiveStarNetTreeNodeRepository;

    /**
     * create rewardNetTree base fiveStarNetTree
     */
    @Override
    public void createRewardNet() {
        FiveStarNetTreeNode fromNode = fiveStarNetTreeNodeRepository.getRootTreeNodeOfMonth(DateUtils.getLastMonthSnapshotDate());

        Stack<TreeNode> stk = new Stack<TreeNode>();
        stk.push(fromNode);

        while (!stk.empty()) {
            TreeNode top = stk.pop();
            DistributorBonus distributorBonus = new DistributorBonus();
            for (TreeNode child : top.getChildNodes()) {
                stk.push(child);
            }
            BeanUtils.copyProperties(top, distributorBonus);
            //find and set uplinkId
            long uplinkId = top.getUplinkId();
            if (uplinkId != 0) {
                FiveStarNetTreeNode one = fiveStarNetTreeNodeRepository.findOne(uplinkId);
                String accountNum = one.getData().getAccountNum();
                DistributorBonus one2 = distributorBonusRepository.getAccountByAccountNum(top.getSnapshotDate(),
                        accountNum);
                distributorBonus.setUplinkId(one2.getId());
            }
            if(distributorBonus.getGpv()>=200){
                calculateReward(distributorBonus);
            }
            distributorBonusRepository.saveAndFlush(distributorBonus);
        }
    }

    @Override
    public TreeNode getRootNode(String lastMonthSnapshotDate) {
        return  distributorBonusRepository.getRootTreeNodeOfMonth(lastMonthSnapshotDate);
    }

    /**
     * calculateDistributorBonus
     * if newFiveStar reward = (gpv-(18000-LMAOPV))*(rate-0.12)
     * and prepare for distributorDifferentialBonus (18000-LMAOPV)*0.12 pass up to up link where pin >newFiveStar
     * if !newFiveStar reward = gpv*(rate-0.12)
     * @param distributorBonus
     */
    private void calculateReward(DistributorBonus distributorBonus) {
        DistributorBonusRate bonusRate = distributorBonusRateRepository.findBonusRateByGpvAndDate(distributorBonus.getGpv(), new Date());
        //if newFiveStar
        if(PinPosition.NEW_FIVE_STAR.equals(distributorBonus.getPin())){
            Float surplus = 18000 - distributorBonus.getAopvLastMonth();
            Float reward = (distributorBonus.getGpv() - surplus)* (bonusRate.getBonusRate() - 0.12F);
            distributorBonus.setReward(reward);
            //surplus opv reward need pass up;will calculate in DistributorDifferentialBonus
            Float surplusReward = surplus * 0.12F;
            DistributorBonus uplink = distributorBonusRepository.findOne(distributorBonus.getUplinkId());
            while(uplink!=null&&PinPosition.NEW_FIVE_STAR.equals(uplink.getData().getPin())){
                uplink = distributorBonusRepository.findOne(uplink.getUplinkId());
            }
            if(uplink!=null){
                uplink.setTemporaryReward(surplusReward);
            }
        }
        else {// if no newFiveStar
            Float reward = distributorBonus.getGpv() * (bonusRate.getBonusRate() - 0.12F);
            distributorBonus.setReward(reward);
        }
    }

}
