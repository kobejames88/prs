package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.reward.DistributorBonus;
import com.perfectchina.bns.model.reward.DistributorBonusRate;
import com.perfectchina.bns.model.reward.LeaderBonus;
import com.perfectchina.bns.model.reward.RubyBonusRate;
import com.perfectchina.bns.model.treenode.FiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.QualifiedFiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.QualifiedFiveStarNetTreeNodeRepository;
import com.perfectchina.bns.repositories.reward.LeaderBonusRepository;
import com.perfectchina.bns.repositories.reward.RubyRateRepository;
import com.perfectchina.bns.service.pin.PinPosition;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Stack;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/14
 * @Desc:
 */
@Service
public class LeaderBonusServiceImpl implements  LeaderBonusService {

    @Autowired
    private LeaderBonusRepository leaderBonusRepository;

    @Autowired
    private RubyRateRepository rubyRateRepository;

    @Autowired
    private QualifiedFiveStarNetTreeNodeRepository qualifiedFiveStarNetTreeNodeRepository;

    /**
     * first copy value from qualifiedFiveStarNet
     */
    @Override
    public void createRewardNet() {
        QualifiedFiveStarNetTreeNode fromNode = qualifiedFiveStarNetTreeNodeRepository.getRootTreeNodeOfMonth(DateUtils.getLastMonthSnapshotDate());
        Stack<TreeNode> stk = new Stack<>();
        stk.push(fromNode);
        while (!stk.isEmpty()){
            TreeNode top = stk.pop();
            for(TreeNode child : top.getChildNodes()){
                stk.push(child);
            }
            LeaderBonus leaderBonus = new LeaderBonus();
            BeanUtils.copyProperties(top, leaderBonus);
            //find and set uplinkId
            long uplinkId = top.getUplinkId();
            if (uplinkId != 0) {
                QualifiedFiveStarNetTreeNode one = qualifiedFiveStarNetTreeNodeRepository.findById(uplinkId).get();
                String accountNum = one.getData().getAccountNum();
                LeaderBonus one2 = leaderBonusRepository.getAccountByAccountNum(top.getSnapshotDate(),
                        accountNum);
                leaderBonus.setUplinkId(one2.getId());
            }
            if(PinPosition.RUBY.equals(leaderBonus.getPin())){
                calculateRubyReward(leaderBonus,(QualifiedFiveStarNetTreeNode)top);
            }
            leaderBonusRepository.saveAndFlush(leaderBonus);
        }
    }

    /**
     * 计算红宝石奖
     * rubyReward = firstChildes.fiveStarIntegral*11% + secondChildes.fiveStarIntegral*2%
     * @param leaderBonus
     */
    private void calculateRubyReward(LeaderBonus leaderBonus,QualifiedFiveStarNetTreeNode fiveStar) {
        RubyBonusRate rubyBonusRate = rubyRateRepository.findBonusRate(new Date()).get(0);
        //*节点
        if(leaderBonus.getAsteriskNodePoints()!=null){
            Float asteriskReward = leaderBonus.getAsteriskNodePoints()*rubyBonusRate.getFirstRate();
            leaderBonus.setRubyReward(leaderBonus.getRubyReward()+asteriskReward);
        }
        //先算第一代
        for(TreeNode first : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(fiveStar.getId())){
            QualifiedFiveStarNetTreeNode firstChild = (QualifiedFiveStarNetTreeNode)first;
            Float firstReward = firstChild.getFiveStarIntegral()*rubyBonusRate.getFirstRate();
            leaderBonus.setRubyReward(leaderBonus.getRubyReward()+firstReward);
            //再算第二代
            for(TreeNode second : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(first.getId())){
                QualifiedFiveStarNetTreeNode secondChild = (QualifiedFiveStarNetTreeNode)second;
                Float secondReward = secondChild.getFiveStarIntegral()*rubyBonusRate.getSecondRate();
                leaderBonus.setRubyReward(leaderBonus.getRubyReward()+secondReward);
            }
        }
    }

    @Override
    public TreeNode getRootNode(String lastMonthSnapshotDate) {
        return leaderBonusRepository.getRootTreeNodeOfMonth(lastMonthSnapshotDate);
    }
}
