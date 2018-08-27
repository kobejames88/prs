package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.common.utils.BeanUtil;
import com.perfectchina.bns.model.reward.GoldenDiamondOPVBonus;
import com.perfectchina.bns.model.reward.GoldenDiamondOPVBonusRate;
import com.perfectchina.bns.model.treenode.GoldDiamondNetTreeNode;
import com.perfectchina.bns.model.treenode.QualifiedFiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.GoldDiamondNetTreeNodeRepository;
import com.perfectchina.bns.repositories.QualifiedFiveStarNetTreeNodeRepository;
import com.perfectchina.bns.repositories.reward.GoldenDiamondOPVBonusRateRepository;
import com.perfectchina.bns.repositories.reward.GoldenDiamondOPVBonusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Stack;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/27
 * @Desc: 金钻平级奖
 */
@Service
public class GoldenDiamondOPVBonusServiceImpl implements  GoldenDiamondOPVBonusService {

    @Autowired
    GoldenDiamondOPVBonusRateService goldenDiamondOPVBonusRateService;

    @Autowired
    GoldenDiamondOPVBonusRepository goldenDiamondOPVBonusRepository;

    @Autowired
    GoldDiamondNetTreeNodeRepository goldDiamondNetTreeNodeRepository;

    @Autowired
    QualifiedFiveStarNetTreeNodeRepository qualifiedFiveStarNetTreeNodeRepository;


    /**
     *  基于金钻网络图计算金钻平级奖
     * @param snapshotDate
     */
    @Override
    public void calculateBonus(String snapshotDate) {
        GoldDiamondNetTreeNode rootTreeNode = goldDiamondNetTreeNodeRepository.getRootTreeNode(snapshotDate);
        Stack<TreeNode> stk = new Stack<>();
        stk.push(rootTreeNode);
        while (stk!=null){
            GoldDiamondNetTreeNode node = (GoldDiamondNetTreeNode)stk.pop();
            List<TreeNode> childNodes = node.getChildNodes();
            for(TreeNode treeNode : childNodes){
                stk.push(treeNode);
            }
            //标记要计算到的代数，待优化
            QualifiedFiveStarNetTreeNode fiveStar = qualifiedFiveStarNetTreeNodeRepository.findByAccountNum(node.getData().getAccountNum(), node.getSnapshotDate());
            int flag = 4;
            if(fiveStar.getGoldDiamondLine()>=7){
                flag = 8;
            }else if(fiveStar.getEmeraldLine()>=7){
                flag = 6;
            }
            float bonus = calculate( node, 0,flag);
            GoldenDiamondOPVBonus goldenDiamondOPVBonus = new GoldenDiamondOPVBonus();
            BeanUtil.copyPropertiesIgnoreNull(node,goldenDiamondOPVBonus);
            goldenDiamondOPVBonus.setReward(bonus);
            goldenDiamondOPVBonusRepository.saveAndFlush(goldenDiamondOPVBonus);
        }
    }

    /**
     * 递归算每一层
     * @param node
     * @param level
     */
    private float calculate(GoldDiamondNetTreeNode node,int level,int flag) {
        level++;
        float  bonus = 0F;
        if(level<=flag){
            Stack<TreeNode> stk = new Stack<>();
            for(TreeNode treeNode : node.getChildNodes()){
                stk.push(treeNode);
                bonus += ((GoldDiamondNetTreeNode)treeNode).getPassUpOpv()*rate(level);
            }
            while (!stk.isEmpty()){
                bonus += calculate((GoldDiamondNetTreeNode)stk.pop(),level,flag);
            }
        }
       return bonus;
    }

    /**
     * 根据代数去获取奖金计算比率
     * @param level
     * @return
     */
    private Float rate(int level) {
        GoldenDiamondOPVBonusRate bonusRate = goldenDiamondOPVBonusRateService.findBonusRateByDate(new Date());
        float rate = 0F;
        switch (level) {
            case 1:
                rate = bonusRate.getBonusRateLvl1();
                break;
            case 2:
                rate = bonusRate.getBonusRateLvl2();
                break;
            case 3:
                rate = bonusRate.getBonusRateLvl3();
                break;
            case 4:
                rate = bonusRate.getBonusRateLvl4();
                break;
            case 5:
                rate = bonusRate.getBonusRateLvl5();
                break;
            case 6:
                rate = bonusRate.getBonusRateLvl6();
                break;
            case 7:
                rate = bonusRate.getBonusRateLvl7();
                break;
            case 8:
                rate = bonusRate.getBonusRateLvl8();
                break;
            default:
                throw new RuntimeException("代数有误");
        }
        return  rate;
    }

    /**
     * 获取snapshotDate 的所有金钻平级奖的信息
     * @param snapshotDate
     * @return
     */
    @Override
    public List<GoldenDiamondOPVBonus> listBonusInfo(String snapshotDate) {

        List<GoldenDiamondOPVBonus> goldenDiamondOPVBonusList =goldenDiamondOPVBonusRepository.findBySnapshotDate(snapshotDate);

        return goldenDiamondOPVBonusList;
    }
}
