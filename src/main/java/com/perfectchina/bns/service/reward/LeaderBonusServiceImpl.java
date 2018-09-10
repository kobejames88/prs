package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.common.utils.MathUtils;
import com.perfectchina.bns.model.reward.LeaderBonus;
import com.perfectchina.bns.model.reward.LeaderBonusRate;
import com.perfectchina.bns.model.treenode.QualifiedFiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.QualifiedFiveStarNetTreeNodeRepository;
import com.perfectchina.bns.repositories.reward.LeaderBonusRepository;
import com.perfectchina.bns.repositories.reward.LeaderRateRepository;
import com.perfectchina.bns.service.pin.PinPosition;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Stack;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/14
 * @Desc: 领导奖计算
 */
@Service
public class LeaderBonusServiceImpl implements  LeaderBonusService {

    @Autowired
    private LeaderBonusRepository leaderBonusRepository;

    @Autowired
    private LeaderRateRepository leaderRateRepository;

    @Autowired
    private QualifiedFiveStarNetTreeNodeRepository qualifiedFiveStarNetTreeNodeRepository;

    /**
     * 基于合格五星图
     * first copy value from qualifiedFiveStarNet
     * snapshotDate yyyyMM
     * 深度优先遍历
     */
    @Override
    public void createRewardNet(String snapshotDate) {
        QualifiedFiveStarNetTreeNode fromNode = qualifiedFiveStarNetTreeNodeRepository.getRootTreeNodeOfMonth(snapshotDate);
        Stack<QualifiedFiveStarNetTreeNode> stk = new Stack<>();
        stk.push(fromNode);
        while (!stk.isEmpty()){
            QualifiedFiveStarNetTreeNode top = stk.pop();
            for(TreeNode child : top.getChildNodes()){
                stk.push((QualifiedFiveStarNetTreeNode)child);
            }
            LeaderBonus leaderBonus = new LeaderBonus();
            BeanUtils.copyProperties(top, leaderBonus);
            //find and set uplinkId ，查找上级并设计leader bonus node 的uplinkId
            long uplinkId = top.getUplinkId();
            if (uplinkId != 0) {
                QualifiedFiveStarNetTreeNode one = qualifiedFiveStarNetTreeNodeRepository.findById(uplinkId).get();
                String accountNum = one.getData().getAccountNum();
                LeaderBonus one2 = leaderBonusRepository.getAccountByAccountNum(top.getSnapshotDate(),
                        accountNum);
                leaderBonus.setUplinkId(one2.getId());
            }
            //pin == 红宝石，计算红宝石奖
            if(PinPosition.RUBY.equals(leaderBonus.getData().getPin())){
                float rubyReward = calculateRubyReward(leaderBonus, (QualifiedFiveStarNetTreeNode) top);
                leaderBonus.setRubyReward(MathUtils.round(rubyReward));
            }
            //pin == 翡翠奖，计算翡翠奖
            if(PinPosition.EMERALD.equals(leaderBonus.getData().getPin())){
                float rubyReward = calculateRubyReward(leaderBonus, (QualifiedFiveStarNetTreeNode) top);
                leaderBonus.setRubyReward(MathUtils.round(rubyReward));
                float emeraldReward = calculateEmeraldReward(leaderBonus, (QualifiedFiveStarNetTreeNode) top);
                leaderBonus.setEmerald(MathUtils.round(emeraldReward));
            }
            //pin == 钻石，计算钻石奖
            if(PinPosition.DIAMOND.equals(leaderBonus.getData().getPin())){
                float rubyReward = calculateRubyReward(leaderBonus, (QualifiedFiveStarNetTreeNode) top);
                leaderBonus.setRubyReward(MathUtils.round(rubyReward));
                float emeraldReward = calculateEmeraldReward(leaderBonus, (QualifiedFiveStarNetTreeNode) top);
                leaderBonus.setEmerald(MathUtils.round(emeraldReward));
                float diamondReward = calculateDiamondReward(leaderBonus, (QualifiedFiveStarNetTreeNode) top);
                leaderBonus.setDiamondReward(MathUtils.round(diamondReward));
            }
            //pin == 金钻，计算金钻奖
            if(PinPosition.GOLD_DIAMOND.equals(leaderBonus.getData().getPin())){
                float rubyReward = calculateRubyReward(leaderBonus, (QualifiedFiveStarNetTreeNode) top);
                leaderBonus.setRubyReward(MathUtils.round(rubyReward));
                float emeraldReward = calculateEmeraldReward(leaderBonus, (QualifiedFiveStarNetTreeNode) top);
                leaderBonus.setEmerald(MathUtils.round(emeraldReward));
                float diamondReward = calculateDiamondReward(leaderBonus, (QualifiedFiveStarNetTreeNode) top);
                leaderBonus.setDiamondReward(MathUtils.round(diamondReward));
                float goldDiamondReward = calculateGoldDiamondReward(leaderBonus, (QualifiedFiveStarNetTreeNode) top);
                leaderBonus.setGoldDiamondReward(MathUtils.round(goldDiamondReward));
            }

            leaderBonusRepository.saveAndFlush(leaderBonus);
        }
    }

    /**
     * 计算金钻奖
     * @param leaderBonus
     * @param top
     * @return
     */
    private float calculateGoldDiamondReward(LeaderBonus leaderBonus, QualifiedFiveStarNetTreeNode top) {
        float reward = 0F;
        LeaderBonusRate leaderBonusRate = leaderRateRepository.findBonusRate(new Date()).get(0);
        Stack<QualifiedFiveStarNetTreeNode> stk = new Stack<>();
        for(QualifiedFiveStarNetTreeNode starNodeFirst : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(top.getId())){
            //第一代的*节点，属于第二代
            reward += starNodeFirst.getAsteriskNodePoints() * leaderBonusRate.getGoldenDiamondgRate();
            //如果第一代是金钻以上的级别，只需算到第二代
            String pin = starNodeFirst.getData().getPin();
            if(PinPosition.DIAMOND.equals(pin)||
                    PinPosition.GOLD_DIAMOND.equals(pin)||
                    PinPosition.DOUBLE_GOLD_DIAMOND.equals(pin)||
                    PinPosition.TRIPLE_GOLD_DIAMOND.equals(pin)){
                for(QualifiedFiveStarNetTreeNode starNodeSecond : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(starNodeFirst.getId())){
                    float emeraldReward = starNodeSecond.getFiveStarIntegral() * leaderBonusRate.getGoldenDiamondgRate();
                    reward += emeraldReward;
                }
            }
            //第二+代入栈
            else{
                for(QualifiedFiveStarNetTreeNode starNodeSecond : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(starNodeFirst.getId())){
                    stk.push(starNodeSecond);
                }
            }

        }
        while (!stk.isEmpty()){
            QualifiedFiveStarNetTreeNode starNetTreeNode = stk.pop();
            String pin = starNetTreeNode.getData().getPin();
            //如果是金钻以上的级别
            if(PinPosition.DIAMOND.equals(pin)||
                    PinPosition.GOLD_DIAMOND.equals(pin)||
                    PinPosition.DOUBLE_GOLD_DIAMOND.equals(pin)||
                    PinPosition.TRIPLE_GOLD_DIAMOND.equals(pin)){
                //先算该节点
                float asterisk = starNetTreeNode.getAsteriskNodePoints() * leaderBonusRate.getGoldenDiamondgRate();
                float emeraldReward = starNetTreeNode.getFiveStarIntegral() * leaderBonusRate.getGoldenDiamondgRate();
                reward += asterisk + emeraldReward;
                //再算子节点
                for (QualifiedFiveStarNetTreeNode starNode : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(starNetTreeNode.getId())) {
                    float emeraldReward2 = starNode.getFiveStarIntegral() * leaderBonusRate.getGoldenDiamondgRate();
                    reward += emeraldReward2;
                }
            }
            //如果是等级低于钻石的
            else {
                float asterisk = starNetTreeNode.getAsteriskNodePoints() * leaderBonusRate.getGoldenDiamondgRate();
                float emeraldReward = starNetTreeNode.getFiveStarIntegral() * leaderBonusRate.getGoldenDiamondgRate();
                reward += asterisk + emeraldReward;
                for (QualifiedFiveStarNetTreeNode starNode : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(starNetTreeNode.getId())) {
                    stk.push(starNode);
                }
            }
        }
        return reward;
    }

    /**
     * 计算钻石奖 第二+代
     * @param leaderBonus
     * @param top
     * @return
     */
    private float calculateDiamondReward(LeaderBonus leaderBonus, QualifiedFiveStarNetTreeNode top) {
       float reward = 0F;
        LeaderBonusRate leaderBonusRate = leaderRateRepository.findBonusRate(new Date()).get(0);
        Stack<QualifiedFiveStarNetTreeNode> stk = new Stack<>();
        for(QualifiedFiveStarNetTreeNode starNodeFirst : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(top.getId())){
            //第一代的*节点，属于第二代
            reward += starNodeFirst.getAsteriskNodePoints() * leaderBonusRate.getDiamondRate();
            //如果第一代是钻石，只需算到第二代
            String pin = starNodeFirst.getData().getPin();
            if(PinPosition.DIAMOND.equals(pin)||
                    PinPosition.GOLD_DIAMOND.equals(pin)||
                    PinPosition.DOUBLE_GOLD_DIAMOND.equals(pin)||
                    PinPosition.TRIPLE_GOLD_DIAMOND.equals(pin)){
                for(QualifiedFiveStarNetTreeNode starNodeSecond : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(starNodeFirst.getId())){
                    float emeraldReward = starNodeSecond.getFiveStarIntegral() * leaderBonusRate.getDiamondRate();
                    reward +=  emeraldReward;
                }
            }
            //第二+代入栈
            else {
                for(QualifiedFiveStarNetTreeNode starNodeSecond : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(starNodeFirst.getId())){
                    stk.push(starNodeSecond);
                }
            }

        }
        while (!stk.isEmpty()){
            QualifiedFiveStarNetTreeNode starNetTreeNode = stk.pop();
            String pin = starNetTreeNode.getData().getPin();
            //如果是钻石以上的级别
            if(PinPosition.DIAMOND.equals(pin)||
                    PinPosition.GOLD_DIAMOND.equals(pin)||
                    PinPosition.DOUBLE_GOLD_DIAMOND.equals(pin)||
                    PinPosition.TRIPLE_GOLD_DIAMOND.equals(pin)){
                //先算该节点
                float asterisk = starNetTreeNode.getAsteriskNodePoints() * leaderBonusRate.getDiamondRate();
                float emeraldReward = starNetTreeNode.getFiveStarIntegral() * leaderBonusRate.getDiamondRate();
                reward += asterisk + emeraldReward;
                //再算子节点
                for (QualifiedFiveStarNetTreeNode starNode : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(starNetTreeNode.getId())) {
                    float emeraldReward2 = starNode.getFiveStarIntegral() * leaderBonusRate.getDiamondRate();
                    reward += emeraldReward2;
                }
            }
            //如果是等级低于钻石的
            else {
                float asterisk = starNetTreeNode.getAsteriskNodePoints() * leaderBonusRate.getDiamondRate();
                float emeraldReward = starNetTreeNode.getFiveStarIntegral() * leaderBonusRate.getDiamondRate();
                reward += asterisk + emeraldReward;
                for (QualifiedFiveStarNetTreeNode starNode : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(starNetTreeNode.getId())) {
                    stk.push(starNode);
                }
            }
        }
        return reward;
    }

    /**
     * 计算翡翠奖
     * @param leaderBonus
     * @param top
     */
    private float calculateEmeraldReward(LeaderBonus leaderBonus, QualifiedFiveStarNetTreeNode top) {
        LeaderBonusRate leaderBonusRate = leaderRateRepository.findBonusRate(new Date()).get(0);
        float reward = 0F;
        //第二+代
        Stack<QualifiedFiveStarNetTreeNode> stk = new Stack<>();
        for (QualifiedFiveStarNetTreeNode starNodeFirst : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(top.getId())) {
            //第一代的*节点，属于第二代
            reward += starNodeFirst.getAsteriskNodePoints() * leaderBonusRate.getEmeraldRate();
            //如果第一代是是翡翠，只需算到第二代
            String pin = starNodeFirst.getData().getPin();
            if(PinPosition.EMERALD.equals(pin)||
                    PinPosition.DIAMOND.equals(pin)||
                    PinPosition.GOLD_DIAMOND.equals(pin)||
                    PinPosition.DOUBLE_GOLD_DIAMOND.equals(pin)||
                    PinPosition.TRIPLE_GOLD_DIAMOND.equals(pin)){
                for(QualifiedFiveStarNetTreeNode starNodeSecond : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(starNodeFirst.getId())){
                    float emeraldReward = starNodeSecond.getFiveStarIntegral() * leaderBonusRate.getEmeraldRate();
                    reward +=  emeraldReward;
                }
            }
            //第二+代入栈
            else{
                for(QualifiedFiveStarNetTreeNode starNodeSecond : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(starNodeFirst.getId())){
                    stk.push(starNodeSecond);
                }
            }

        }
        while (!stk.isEmpty()){
            QualifiedFiveStarNetTreeNode starNetTreeNode = stk.pop();
            String pin = starNetTreeNode.getData().getPin();
            //如果是翡翠以上的级别
            if(PinPosition.EMERALD.equals(pin)||
                    PinPosition.DIAMOND.equals(pin)||
                    PinPosition.GOLD_DIAMOND.equals(pin)||
                    PinPosition.DOUBLE_GOLD_DIAMOND.equals(pin)||
                    PinPosition.TRIPLE_GOLD_DIAMOND.equals(pin)){
                //先算该节点
                float asterisk = starNetTreeNode.getAsteriskNodePoints() * leaderBonusRate.getEmeraldRate();
                float emeraldReward = starNetTreeNode.getFiveStarIntegral() * leaderBonusRate.getEmeraldRate();
                reward += asterisk + emeraldReward;
                //再算子节点，然后子节点不入栈
                for (QualifiedFiveStarNetTreeNode starNode : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(starNetTreeNode.getId())) {
                    float emeraldReward2 = starNode.getFiveStarIntegral() * leaderBonusRate.getEmeraldRate();
                    reward += emeraldReward2;
                }
            }
            //如果是等级低于翡翠的
            else {
                float asterisk = starNetTreeNode.getAsteriskNodePoints() * leaderBonusRate.getEmeraldRate();
                float emeraldReward = starNetTreeNode.getFiveStarIntegral() * leaderBonusRate.getEmeraldRate();
                reward += asterisk + emeraldReward;
                for (QualifiedFiveStarNetTreeNode starNode : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(starNetTreeNode.getId())) {
                    stk.push(starNode);
                }
            }
        }
        return reward;
    }

    /**
     * 计算红宝石奖
     * rubyReward = (firstChildes.fiveStarIntegral+top.*节点)*11% + （firstChildes.*节点+ secondChildes.fiveStarIntegral）*2%
     * @param leaderBonus
     */
    private float calculateRubyReward(LeaderBonus leaderBonus,QualifiedFiveStarNetTreeNode fiveStar) {
        float reward = 0F;
        LeaderBonusRate leaderBonusRate = leaderRateRepository.findBonusRate(new Date()).get(0);
        //该点的*节点,以第一代比率算
        if(leaderBonus.getHasAsteriskNode()!=null&&leaderBonus.getHasAsteriskNode()){
            Float asteriskReward = leaderBonus.getAsteriskNodePoints()* leaderBonusRate.getRubyFirstRate();
           // leaderBonus.setRubyReward(leaderBonus.getRubyReward()+asteriskReward);
            reward += asteriskReward;
        }
        //先算第一代
        for(TreeNode first : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(fiveStar.getId())){
            QualifiedFiveStarNetTreeNode firstChild = (QualifiedFiveStarNetTreeNode)first;
            Float firstReward = firstChild.getFiveStarIntegral()* leaderBonusRate.getRubyFirstRate();
            //leaderBonus.setRubyReward(leaderBonus.getRubyReward()+firstReward);
            reward += firstReward;
            //第一代的*节点，以第二代比率算
            if(firstChild.getHasAsteriskNode()){
                Float asteriskReward = firstChild.getAsteriskNodePoints()* leaderBonusRate.getRubySecondRate();
                //leaderBonus.setRubyReward(leaderBonus.getRubyReward()+asteriskReward);
                reward += asteriskReward;
            }

            //再算第二代
            for(TreeNode second : qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(first.getId())){
                QualifiedFiveStarNetTreeNode secondChild = (QualifiedFiveStarNetTreeNode)second;
                Float secondReward = secondChild.getFiveStarIntegral()* leaderBonusRate.getRubySecondRate();
                //leaderBonus.setRubyReward(leaderBonus.getRubyReward()+secondReward);
                reward += secondReward;
            }
        }
        return reward;
    }

    @Override
    public TreeNode getRootNode(String lastMonthSnapshotDate) {
        return leaderBonusRepository.getRootTreeNodeOfMonth(lastMonthSnapshotDate);
    }
}
