package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.model.reward.ContinuedProgressBonus;
import com.perfectchina.bns.model.reward.ContinuedProgressRecord;
import com.perfectchina.bns.model.treenode.QualifiedFiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.SimpleNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.OpvNetTreeNodeRepository;
import com.perfectchina.bns.repositories.SimpleNetTreeNodeRepository;
import com.perfectchina.bns.repositories.reward.ContinuedProgressBonusRepository;
import com.perfectchina.bns.repositories.reward.ContinuedProgressRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Stack;

/**
 * @Author: chenhuahai
 * @Date: 2018/9/6
 * @Desc: 持续进步奖
 */
@Service
public class ContinuedProgressBonusServiceImpl implements ContinuedProgressBonusService{

    @Autowired
    private OpvNetTreeNodeRepository opvNetTreeNodeRepository;

    @Autowired
    private SimpleNetTreeNodeRepository simpleNetTreeNodeRepository;

    @Autowired
    private ContinuedProgressRecordRepository continuedProgressRecordRepository;

    @Autowired
    private ContinuedProgressBonusRepository continuedProgressBonusRepository;

    /**
     * 计算持续进步奖: 要先算持续进步得分
     * 算持续进步得分基于合格五星图
     * 算持续进步奖基于原始网络图
     * 深度优先遍历
     * @param snapshotDate yyyy-MM-MM-MM
     * @param quarter 季度
     */
    @Override
    public void calculateContinuedProgressBonus(String snapshotDate,int quarter) {
        String[] snapshotDates = getSnapshotDates(snapshotDate,quarter);
        //1、获取公司季度opv
        float companyOpv = 0F;
        for(String snapDate : snapshotDates){
            companyOpv += opvNetTreeNodeRepository.getRootTreeNode(snapDate).getOpv();
        }
        //2、所有会员季度总分
        Integer companyTotalPoint = 0;
        companyTotalPoint = calculateTotalPoint(snapshotDates,snapshotDate);
        //季度总奖金；TODO：计算比率要存到配置项中
        float totalBonus = companyOpv * 0.015F;
        //3、计算个人得奖
        SimpleNetTreeNode treeNode = simpleNetTreeNodeRepository.getRootTreeNode(snapshotDates[snapshotDates.length-1]);
        Stack<TreeNode> stk = new Stack<>();
        stk.push(treeNode);
        while(!stk.isEmpty()) {
            SimpleNetTreeNode simpleNetTreeNode = (SimpleNetTreeNode) stk.pop();
            for (TreeNode child : simpleNetTreeNode.getChildNodes()) {
                stk.push(child);
            }
            //snapshotDate yyyy-MM-MM-MM
            long id = simpleNetTreeNode.getData().getId();
            long a = id;
            ContinuedProgressBonus continuedProgressBonus = continuedProgressBonusRepository.findContinuedProgressBonus(snapshotDate, simpleNetTreeNode.getData().getId());
            //加权分值
            int totalPoint = continuedProgressBonus.getTotalPoint();
            float avgPoint = continuedProgressBonus.getTotalPoint()*1.0F / companyTotalPoint;
            float bonus = totalBonus * avgPoint;
            continuedProgressBonus.setBonus(bonus);
            continuedProgressBonusRepository.saveAndFlush(continuedProgressBonus);
        }
    }

    /**
     * 基于原始树
     * 计算个人季度得分，及所有会员季度总分
     */
    private Integer calculateTotalPoint(String[] snapshotDates,String snapshotDate) {
        //计算个人季度得分，及所有会员季度总分
        SimpleNetTreeNode treeNode = simpleNetTreeNodeRepository.getRootTreeNode(snapshotDates[snapshotDates.length-1]);
        Stack<TreeNode> stk = new Stack<>();
        stk.push(treeNode);
        Integer companyTotalPoint = 0;
        while(!stk.isEmpty()){
            SimpleNetTreeNode simpleNetTreeNode = (SimpleNetTreeNode)stk.pop();
            for(TreeNode child : simpleNetTreeNode.getChildNodes()){
                stk.push(child);
            }
            Integer personTotalPoint = 0;
            for(String snapDate : snapshotDates){
                ContinuedProgressRecord continuedProgressRecord = continuedProgressRecordRepository.findByAccountNum(snapDate, simpleNetTreeNode.getData().getId());
                if(continuedProgressRecord!=null){
                    personTotalPoint += continuedProgressRecord.getTotal();
                }
            }
            ContinuedProgressBonus continuedProgressBonus = new ContinuedProgressBonus();
            continuedProgressBonus.setTotalPoint(personTotalPoint);
            continuedProgressBonus.setAccount(simpleNetTreeNode.getData());
            continuedProgressBonus.setSnapshotDate(snapshotDate);
            continuedProgressBonusRepository.saveAndFlush(continuedProgressBonus);
            companyTotalPoint += personTotalPoint;
        }
        return companyTotalPoint;
    }

    /**
     * 拼接日期snapshotDate
     * @param snapshotDate
     * @param quarter
     * @return
     */
    private String[] getSnapshotDates(String snapshotDate,int quarter) {
        snapshotDate = snapshotDate.substring(0,4);
        String snapshotDate1 = null;
        String snapshotDate2 = null;
        String snapshotDate3 = null;
        if(quarter==1){
            snapshotDate1 = snapshotDate+"12";
            snapshotDate2 = snapshotDate+"01";
            snapshotDate3 = snapshotDate+"02";
        }else  if(quarter==2){
            snapshotDate1 = snapshotDate+"03";
            snapshotDate2 = snapshotDate+"04";
            snapshotDate3 = snapshotDate+"05";
        }else  if(quarter==3){
            //snapshotDate1 = snapshotDate+"06";
            snapshotDate2 = snapshotDate+"07";
            snapshotDate3 = snapshotDate+"08";
        }else  if(quarter==4){
            snapshotDate1 = snapshotDate+"09";
            snapshotDate2 = snapshotDate+"10";
            snapshotDate3 = snapshotDate+"11";
        }
        //TODO:在做测试
        //String[] snapshotDates = {snapshotDate1,snapshotDate2,snapshotDate3};
        String[] snapshotDates = {snapshotDate2,snapshotDate3};
        return snapshotDates;
    }
}
