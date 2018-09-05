package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.model.reward.ContinuedProgressRecord;
import com.perfectchina.bns.model.treenode.QualifiedFiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.QualifiedFiveStarNetTreeNodeRepository;
import com.perfectchina.bns.repositories.reward.ContinuedProgressRecordRepository;
import com.perfectchina.bns.service.Enum.ContinuedProgressRateEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Stack;

/**
 * @Author: chenhuahai
 * @Date: 2018/9/5
 * @Desc:  持续进步得分记录 ,基于合格五星图计算
 */

public class ContinuedProgressRecordServiceImpl implements ContinuedProgressRecordService{

    @Autowired
    private QualifiedFiveStarNetTreeNodeRepository qualifiedFiveStarNetTreeNodeRepository;

    @Autowired
    private ContinuedProgressRecordRepository continuedProgressRecordRepository;


    /**
     * 计算持续进步得分
     * 深度优先遍历
     */
    @Override
    public void calculateProgressRecord(String snapshotDate) {
        QualifiedFiveStarNetTreeNode treeNode = qualifiedFiveStarNetTreeNodeRepository.getRootTreeNode(snapshotDate);
        Stack<TreeNode> stk = new Stack<>();
        stk.push(treeNode);
        while (!stk.isEmpty()){
            QualifiedFiveStarNetTreeNode fiveStarNode = (QualifiedFiveStarNetTreeNode)stk.pop();
            for(TreeNode childNode : fiveStarNode.getChildNodes()){
                stk.push(childNode);
            }
            //开始计算
            calculateContinued(fiveStarNode);

        }
    }

    /**
     * 连续：
     * 1、初始为0
     * 2、达到某职级，该职级和一下职级连续在职级月数=上月数+1，连续不在职级月数=0；
     * 以上职级，连续在职级月数=0，连续不在职级月数=上月数+1；
     * @param fiveStarNode
     */
    private void calculateContinued(QualifiedFiveStarNetTreeNode fiveStarNode) {
        //当月
        ContinuedProgressRecord continuedProgressRecord = new ContinuedProgressRecord(0);
        //上月记录
        ContinuedProgressRecord continuedProgressRecordLM = continuedProgressRecordRepository.findByAccountNum(fiveStarNode.getSnapshotDate(), fiveStarNode.getData().getId());
        switch (fiveStarNode.getData().getPin()){
            case "TRI_GOLD_DIA":
            case "DOU_GOLD_DIA":
            case "GOLD_DIA":
                continuedProgressRecord.setNewCountR(continuedProgressRecordLM.getNewCountR()+1);
                continuedProgressRecord.setNewCountE(continuedProgressRecordLM.getNewCountE()+1);
                continuedProgressRecord.setNewCountD(continuedProgressRecordLM.getNewCountD()+1);
                continuedProgressRecord.setNewCountG(continuedProgressRecordLM.getNewCountG()+1);
                calculatePointG( continuedProgressRecord, continuedProgressRecordLM);
                break;
            case "DIAMOND":
                continuedProgressRecord.setNewCountR(continuedProgressRecordLM.getNewCountR()+1);
                continuedProgressRecord.setNewCountE(continuedProgressRecordLM.getNewCountE()+1);
                continuedProgressRecord.setNewCountD(continuedProgressRecordLM.getNewCountD()+1);
                continuedProgressRecord.setOldCountG(continuedProgressRecordLM.getOldCountG()+1);
                calculatePointD( continuedProgressRecord, continuedProgressRecordLM);
                break;
            case "EMERALD":
                continuedProgressRecord.setNewCountR(continuedProgressRecordLM.getNewCountR()+1);
                continuedProgressRecord.setNewCountE(continuedProgressRecordLM.getNewCountE()+1);
                continuedProgressRecord.setOldCountD(continuedProgressRecordLM.getOldCountD()+1);
                continuedProgressRecord.setOldCountG(continuedProgressRecordLM.getOldCountG()+1);
                calculatePointE( continuedProgressRecord, continuedProgressRecordLM);
                break;
            case "RUBY":
                continuedProgressRecord.setNewCountR(continuedProgressRecordLM.getNewCountR()+1);
                continuedProgressRecord.setOldCountE(continuedProgressRecordLM.getOldCountE()+1);
                continuedProgressRecord.setOldCountD(continuedProgressRecordLM.getOldCountD()+1);
                continuedProgressRecord.setOldCountG(continuedProgressRecordLM.getOldCountG()+1);
                calculatePointR( continuedProgressRecord, continuedProgressRecordLM);
                break;
            default:
                //不是新晋持续月数+1
                continuedProgressRecord.setOldCountR(continuedProgressRecordLM.getOldCountR()+1);
                continuedProgressRecord.setOldCountE(continuedProgressRecordLM.getOldCountE()+1);
                continuedProgressRecord.setOldCountD(continuedProgressRecordLM.getOldCountD()+1);
                continuedProgressRecord.setOldCountG(continuedProgressRecordLM.getOldCountG()+1);
                break;
        }
        continuedProgressRecordRepository.saveAndFlush(continuedProgressRecord);
    }

    /**
     * 根据职级，计算该职级得分
     * 计算红宝石得分
     */
    private void calculatePointR(ContinuedProgressRecord continuedProgressRecord,ContinuedProgressRecord continuedProgressRecordLM){

        Integer newCountR = continuedProgressRecordLM.getNewCountE();
        Integer oldCountR = continuedProgressRecordLM.getOldCountE();
        //计算得分的两种条件：1、连续在职级<12  2、连续不在职级 > 12
        if(newCountR< ContinuedProgressRateEnum.rateOf("NEWCOUNT")||oldCountR> ContinuedProgressRateEnum.rateOf("OLDCOUNT")){
            continuedProgressRecord.setR(continuedProgressRecordLM.getR()+ContinuedProgressRateEnum.rateOf("EMERALD"));
            //TODO:把积分贡献给上级
        }
    }

    /**
     * 计算翡翠得分
     * @param continuedProgressRecord
     * @param continuedProgressRecordLM
     */
    private void calculatePointE(ContinuedProgressRecord continuedProgressRecord,ContinuedProgressRecord continuedProgressRecordLM){

        Integer newCountE = continuedProgressRecordLM.getNewCountE();
        Integer oldCountE = continuedProgressRecordLM.getOldCountE();
        //计算得分的两种条件：1、连续在职级<12  2、连续不在职级 > 12
        if(newCountE< ContinuedProgressRateEnum.rateOf("NEWCOUNT")||oldCountE> ContinuedProgressRateEnum.rateOf("OLDCOUNT")){
            continuedProgressRecord.setE(continuedProgressRecordLM.getE()+ContinuedProgressRateEnum.rateOf("EMERALD"));
            //TODO:把积分贡献给上级
        }
        //计算红宝石得分
        calculatePointR( continuedProgressRecord, continuedProgressRecordLM);
    }

    //计算钻石
    private void calculatePointD(ContinuedProgressRecord continuedProgressRecord,ContinuedProgressRecord continuedProgressRecordLM){

        Integer newCountD = continuedProgressRecordLM.getNewCountD();
        Integer oldCountD = continuedProgressRecordLM.getOldCountD();
        //计算得分的两种条件：1、连续在职级<12  2、连续不在职级 > 12
        if(newCountD< ContinuedProgressRateEnum.rateOf("NEWCOUNT")||oldCountD> ContinuedProgressRateEnum.rateOf("OLDCOUNT")){
            continuedProgressRecord.setD(continuedProgressRecordLM.getD()+ContinuedProgressRateEnum.rateOf("DIAMOND"));
            //TODO:把积分贡献给上级
        }
        //计算翡翠得分
        calculatePointE( continuedProgressRecord, continuedProgressRecordLM);
        //计算红宝石得分
        calculatePointR( continuedProgressRecord, continuedProgressRecordLM);
    }

    //计算金钻得分
    private void calculatePointG(ContinuedProgressRecord continuedProgressRecord,ContinuedProgressRecord continuedProgressRecordLM){

        Integer newCountG = continuedProgressRecordLM.getNewCountG();
        Integer oldCountG = continuedProgressRecordLM.getOldCountG();
        //计算得分的两种条件：1、连续在职级<12  2、连续不在职级 > 12
        if(newCountG< ContinuedProgressRateEnum.rateOf("NEWCOUNT")||oldCountG> ContinuedProgressRateEnum.rateOf("OLDCOUNT")){
            continuedProgressRecord.setG(continuedProgressRecordLM.getG()+ContinuedProgressRateEnum.rateOf("GOLD_DIA"));
            //TODO:把积分贡献给上级
        }
        //计算钻石得分
        calculatePointD( continuedProgressRecord, continuedProgressRecordLM);
        //计算翡翠得分
        calculatePointE( continuedProgressRecord, continuedProgressRecordLM);
        //计算红宝石得分
        calculatePointR( continuedProgressRecord, continuedProgressRecordLM);
    }

}
