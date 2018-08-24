package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.common.utils.ManthUtils;
import com.perfectchina.bns.model.reward.DistributorBonus;
import com.perfectchina.bns.model.reward.DistributorDifferentialBonus;
import com.perfectchina.bns.model.treenode.FiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.reward.DistributorBonusRateRepository;
import com.perfectchina.bns.repositories.reward.DistributorBonusRepository;
import com.perfectchina.bns.repositories.reward.DistributorDifferentialBonusRateRepository;
import com.perfectchina.bns.repositories.reward.DistributorDifferentialBonusRepository;
import org.hibernate.dialect.pagination.TopLimitHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Stack;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/8
 * @Desc:
 */
@Service
public class DistributorDifferentialBonusNetServiceImpl implements DistributorDifferentialBonusNetService {



    @Autowired
    private DistributorDifferentialBonusRepository distributorDifferentialBonusRepository;

    @Autowired
    private DistributorBonusRepository distributorBonusRepository;

    @Autowired
    private DistributorDifferentialBonusRateRepository distributorDifferentialBonusRateRepository;

    /**
     * calculate distributorDifferentialBonus base on distributorBonus
     */
    @Override
    public void createRewardNet(String snapshotDate) {
        DistributorBonus fromNode = distributorBonusRepository.getRootTreeNodeOfMonth(snapshotDate);
        Stack<TreeNode> stk = new Stack<>();
        stk.push(fromNode);
        while (!stk.empty()){
            TreeNode top = stk.pop();
            for(TreeNode child : top.getChildNodes()){
                stk.push(child);
            }
            DistributorDifferentialBonus distributorDifferentialBonus = new DistributorDifferentialBonus();
            BeanUtils.copyProperties(top,distributorDifferentialBonus);
            //find and set uplinkId
            long uplinkId = top.getUplinkId();
            if (uplinkId != 0) {
                DistributorBonus one = distributorBonusRepository.findById(uplinkId).get();
                String accountNum = one.getData().getAccountNum();
                DistributorDifferentialBonus one2 = distributorDifferentialBonusRepository.getAccountByAccountNum(top.getSnapshotDate(),
                        accountNum);
                distributorDifferentialBonus.setUplinkId(one2.getId());
            }
            calculateReward(distributorDifferentialBonus,(DistributorBonus)top);
            distributorDifferentialBonusRepository.saveAndFlush(distributorDifferentialBonus);
        }
    }

    @Override
    public TreeNode getRootNode(String lastMonthSnapshotDate) {
        return distributorDifferentialBonusRepository.getRootTreeNodeOfMonth(lastMonthSnapshotDate);
    }

    /**
     * 1、have down line reward = (opv*rate - all child.opv*child.rate) + temporaryReward  - distributorBonus
     * 2、no have down line reward = 0
     * 3、newFiveStar 18000 before ,has calculate in distributorBonus temporaryReward
     * @param distributorDifferentialBonus
     * @param distributorBonus
     */
    private void calculateReward(DistributorDifferentialBonus distributorDifferentialBonus,DistributorBonus distributorBonus) {
        if(distributorBonus.getChildNodes()!=null&&distributorBonus.getChildNodes().size()>0){
            Float childReward = 0F;

            for(DistributorBonus childNode :  distributorBonusRepository.getChildNodesByUpid(distributorBonus.getId())){
                DistributorBonus child = childNode;
                childReward += child.getOpv()*(distributorDifferentialBonusRateRepository.findBonusRateByOpvAndDateAsc(child.getOpv(),new Date()).getBonusRate());
            }
            Float reward = distributorDifferentialBonus.getOpv()*(distributorDifferentialBonusRateRepository.findBonusRateByOpvAndDateAsc(distributorDifferentialBonus.getOpv(),new Date()).getBonusRate());
            reward = reward - childReward + distributorBonus.getTemporaryReward() -distributorBonus.getTemporaryBonus();
            distributorDifferentialBonus.setReward(ManthUtils.round(reward));
        }
        else{
            distributorDifferentialBonus.setReward(0);
        }
    }

}
