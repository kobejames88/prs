package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.model.reward.TripleGoldenDiamondBonus;
import com.perfectchina.bns.model.reward.TripleGoldenDiamondBonusRate;
import com.perfectchina.bns.model.treenode.DoubleGoldDiamondNetTreeNode;
import com.perfectchina.bns.model.treenode.OpvNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.DoubleGoldDiamondNetTreeNodeRepository;
import com.perfectchina.bns.repositories.OpvNetTreeNodeRepository;
import com.perfectchina.bns.repositories.reward.TripleGoldenDiamondBonusRateRepository;
import com.perfectchina.bns.repositories.reward.TripleGoldenDiamondBonusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/29
 * @Desc: 三金钻奖
 */
@Service
public class TripleGoldenDiamondBonusServiceImpl implements TripleGoldenDiamondBonusService {

    @Autowired
    private TripleGoldenDiamondBonusRepository tripleGoldenDiamondBonusRepository;

    @Autowired
    private TripleGoldenDiamondBonusRateRepository tripleGoldenDiamondBonusRateRepository;

    @Autowired
    private DoubleGoldDiamondNetTreeNodeRepository doubleGoldDiamondNetTreeNodeRepository;

    @Autowired
    private OpvNetTreeNodeRepository opvNetTreeNodeRepository;


    /**
     *  基于双金钻网络图
     * 计算三金钻奖
     * @param snapshotDate
     */
    @Override
    public void calculateBonus(String snapshotDate) {
        DoubleGoldDiamondNetTreeNode rootTreeNode = doubleGoldDiamondNetTreeNodeRepository.getRootTreeNode(snapshotDate);
        //记录符合三金钻奖条件的节点
        List<DoubleGoldDiamondNetTreeNode> TripleGoldenDiamondBonusNodes = new ArrayList<>();
        Stack<TreeNode> stk = new Stack<>();
        stk.push(rootTreeNode);
        while (!stk.isEmpty()){
            DoubleGoldDiamondNetTreeNode node = (DoubleGoldDiamondNetTreeNode)stk.pop();
            OpvNetTreeNode opvNetTreeNode = opvNetTreeNodeRepository.findByAccountNum(node.getSnapshotDate(), node.getData().getAccountNum());
            //本人是合格双钻，由7条双钻线
            if(opvNetTreeNode.getPpv()>=18000 && node.getChildNodes()!=null && node.getChildNodes().size()>=7 ){
                TripleGoldenDiamondBonusNodes.add(node);
            }
            for(TreeNode childNode : node.getChildNodes()){
                stk.push(childNode);
            }
        }

        calculate(TripleGoldenDiamondBonusNodes);
    }

    private void calculate(List<DoubleGoldDiamondNetTreeNode> tripleGoldenDiamondBonusNodes) {
        if(tripleGoldenDiamondBonusNodes!=null&&tripleGoldenDiamondBonusNodes.size()>0){
            //三金钻计算比率
            TripleGoldenDiamondBonusRate tripleGoldenDiamondBonusRate = tripleGoldenDiamondBonusRateRepository.findBonusRateByDate(new Date()).get(0);
            //公司opv
            Float companyOpv = opvNetTreeNodeRepository.getRootTreeNode(tripleGoldenDiamondBonusNodes.get(0).getSnapshotDate()).getOpv();
            //其中，0.05%按达标三钻人数作加权平均
            float tripleDiamondAvgBonus = (companyOpv * tripleGoldenDiamondBonusRate.getTripleDiamondRate())/tripleGoldenDiamondBonusNodes.size();
            //另外0.05%按所有达标双钻所领取的双钻奖金额作加权平均
            float doubleDiamondBonusCompany = companyOpv * tripleGoldenDiamondBonusRate.getDoubleDiamondRate();

            //获取所有达标双钻所领取的双钻奖金额,总的 TODO: 到双金钻奖的表中查
            float totalDoubleDiamondBonus = 1F;
            for(DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode : tripleGoldenDiamondBonusNodes){
                totalDoubleDiamondBonus += doubleGoldDiamondNetTreeNodeRepository.findByAccountId(doubleGoldDiamondNetTreeNode.getSnapshotDate(),doubleGoldDiamondNetTreeNode.getData().getId()).getRewardBonus().floatValue();
            }

            for(DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode : tripleGoldenDiamondBonusNodes){

                //获取自己的双金钻奖
                float doubleDiamondBonus = 1F; //TODO: 到双金钻奖的表中查
                doubleDiamondBonus = doubleGoldDiamondNetTreeNodeRepository.findByAccountId(doubleGoldDiamondNetTreeNode.getSnapshotDate(),doubleGoldDiamondNetTreeNode.getData().getId()).getRewardBonus().floatValue();

                //双钻奖金额作加权平均
                float doubleDiamondReward = doubleDiamondBonusCompany * (doubleDiamondBonus/totalDoubleDiamondBonus);
                TripleGoldenDiamondBonus tripleGoldenDiamondBonus = new TripleGoldenDiamondBonus();
                //三金钻奖，
                tripleGoldenDiamondBonus.setBonus(tripleDiamondAvgBonus+doubleDiamondReward);
                tripleGoldenDiamondBonus.setAccount(doubleGoldDiamondNetTreeNode.getData());
                tripleGoldenDiamondBonus.setSnapshotDate(doubleGoldDiamondNetTreeNode.getSnapshotDate());
                tripleGoldenDiamondBonus.setLastUpdatedDate(new Date());
                tripleGoldenDiamondBonusRepository.saveAndFlush(tripleGoldenDiamondBonus);
            }
        }
    }

    /**
     * 按时间获取对应所有三金钻奖
     * @param snapshotDate
     * @return
     */
    @Override
    public List<TripleGoldenDiamondBonus> listBonusInfo(String snapshotDate) {
        List<TripleGoldenDiamondBonus> tripleDiamondBonusList = tripleGoldenDiamondBonusRepository.findBySnapshotDate(snapshotDate);
        return tripleDiamondBonusList;
    }
}
