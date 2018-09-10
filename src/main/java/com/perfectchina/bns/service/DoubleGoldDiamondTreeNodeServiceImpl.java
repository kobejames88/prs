package com.perfectchina.bns.service;

import com.perfectchina.bns.common.utils.BigDecimalUtil;
import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.common.utils.SavePinUtils;
import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.AccountPinHistory;
import com.perfectchina.bns.model.PassUpGpv;
import com.perfectchina.bns.model.treenode.*;
import com.perfectchina.bns.model.vo.DoubleGoldDiamonndVo;
import com.perfectchina.bns.model.vo.GoldDiamonndVo;
import com.perfectchina.bns.repositories.*;
import com.perfectchina.bns.service.Enum.Pin;
import com.perfectchina.bns.service.Enum.RewardType;
import com.perfectchina.bns.service.pin.PinPosition;
import com.perfectchina.bns.service.reward.RewardPosition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DoubleGoldDiamondTreeNodeServiceImpl extends TreeNodeServiceImpl implements DoubleGoldDiamondTreeNodeService {

    private static final Logger logger = LoggerFactory.getLogger(DoubleGoldDiamondTreeNodeServiceImpl.class);

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);

    @Autowired
    private GoldDiamondNetTreeNodeRepository goldDiamondNetTreeNodeRepository;

    @Autowired
    private DoubleGoldDiamondNetTreeNodeRepository doubleGoldDiamondNetTreeNodeRepository;

    @Autowired
    private OpvNetTreeNodeRepository opvNetTreeNodeRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountPinHistoryRepository accountPinHistoryRepository;

    private Date previousDateEndTime; // Parameter to set calculate PPV for
    // which month

    public Date getPreviousDateEndTime() {
        return previousDateEndTime;
    }

    public void setPreviousDateEndTime(Date previousDateEndTime) {
        this.previousDateEndTime = previousDateEndTime;
    }

    // Need to walk through simple net, therefore, return simple net tree node
    // repository
    public TreeNodeRepository<DoubleGoldDiamondNetTreeNode> getTreeNodeRepository() {
        return doubleGoldDiamondNetTreeNodeRepository;
    }

    public boolean isReadyToUpdate() {
        // need to check if Simple Net already exist, otherwise, cannot
        // calculate
        boolean isReady = false;
        // // Current month
        String snapshotDate = null;
        try {
            snapshotDate = sdf.format(getPreviousDateEndTime());
            GoldDiamondNetTreeNode rootNode = goldDiamondNetTreeNodeRepository.getRootTreeNodeOfMonth(snapshotDate);

            if (rootNode != null) {
                isReady = true;
            }
        } catch (Exception ex) {
            logger.error("isReadyToUpdate, invalidDate=" + getPreviousDateEndTime());
        }
        return isReady;
    }

    @Override
    public void updateWholeTree(String snapshotDate) {
        TreeNode rootNode = goldDiamondNetTreeNodeRepository.getRootTreeNode(snapshotDate);
        updateChildTreeLevel(0, rootNode, snapshotDate);
    }

    @Override
    public void updateChildTreeLevel(Integer fromLevelNum, TreeNode fromNode, String snapshotDate) {
        super.updateChildTreeLevel(fromLevelNum, fromNode, snapshotDate);
    }

    public int getMaxTreeLevel(String snapShotDate) {
        Integer maxLevelNum = getTreeNodeRepository().getMaxLevelNum(snapShotDate);
        return maxLevelNum == null ? -1 : maxLevelNum;
    }

    private Map<Long, Long> relation = new HashMap<>();
    private int DoubleRewardCount = 3;
    private int TripleRewardCount = 4;
    private int QuadrupleRewardCount = 1;
    private BigDecimal totalPassupopv = new BigDecimal(0);

    /**
     * param node is SimpleNetTreeNode walking through
     */
    @Override
    protected void process(TreeNode node, String snapshotDate) {
        logger.debug("process, update node=" + node.getData().getAccountNum() + "/" + node.getData().getName()
                + ", level [" + node.getLevelNum() + "].");
        // 当前元素
        GoldDiamondNetTreeNode goldDiamondNetTreeNode = (GoldDiamondNetTreeNode) node;
        // 待装载元素
        DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode = new DoubleGoldDiamondNetTreeNode();
        // 当前元素id
        Long id = goldDiamondNetTreeNode.getId();
        // 当前元素上级id
        long uplinkId = goldDiamondNetTreeNode.getUplinkId();
        // 获取当前元素的所有直接下级
        List<GoldDiamondNetTreeNode> childNodes = goldDiamondNetTreeNodeRepository.getChildNodesByUpid(id);
        int count = childNodes.size();
        // 根节点直接通过
        if (uplinkId == 0) {
            if (count > 0) {
                for (GoldDiamondNetTreeNode childNode : childNodes) {
                    relation.put(childNode.getId(), id);
                }
            }
            copyNetTree(goldDiamondNetTreeNode, doubleGoldDiamondNetTreeNode);
            return;
        }

        Boolean isDoubleGoldDiamond = goldDiamondNetTreeNode.getData().getPin() == PinPosition.DOUBLE_GOLD_DIAMOND;
        if (count > 0) {
            // 当前节点有下级
            for (GoldDiamondNetTreeNode childNode : childNodes) {
                if (!isDoubleGoldDiamond) {
                    // 如果不是双金钻职级
                    relation.put(childNode.getId(), uplinkId);
                } else {
                    // 如果是双金钻职级

                    relation.put(childNode.getId(), id);
                    totalPassupopv =  totalPassupopv.add(BigDecimalUtil.multiply(0.001D,goldDiamondNetTreeNode.getPassUpOpv().doubleValue()));
                    countReward(goldDiamondNetTreeNode);
                    buildNode(doubleGoldDiamondNetTreeNode, goldDiamondNetTreeNode, id);
                }
            }
        }

        relation.remove(id);
    }

    private void countReward(GoldDiamondNetTreeNode goldDiamondNetTreeNode){
        switch (RewardType.descOf(goldDiamondNetTreeNode.getReward()).getCode()) {
            case 2:
                DoubleRewardCount += 1;
                break;
            case 3:
                DoubleRewardCount += 1;
                TripleRewardCount += 1;
                break;
            case 4:
                DoubleRewardCount += 1;
                TripleRewardCount += 1;
                QuadrupleRewardCount += 1;
                break;
        }

    }

    private void buildNode(DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode, GoldDiamondNetTreeNode goldDiamondNetTreeNode, Long id) {
        setUplinkid(doubleGoldDiamondNetTreeNode, goldDiamondNetTreeNode, id);
        copyNetTree(goldDiamondNetTreeNode, doubleGoldDiamondNetTreeNode);
    }

    private void setUplinkid(DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode, GoldDiamondNetTreeNode goldDiamondNetTreeNode, Long id) {
        // 获取双金钻上级的id，并通过id获取节点
        Long map_uplinkId = relation.get(id);
        if (map_uplinkId != null && map_uplinkId > 0) {
            GoldDiamondNetTreeNode goldDiamondUplink = goldDiamondNetTreeNodeRepository.getOne(map_uplinkId);
            String uplinkAccountNum = goldDiamondUplink.getData().getAccountNum();
            DoubleGoldDiamondNetTreeNode doubleGoldDiamondUplink = getTreeNodeRepository().getAccountByAccountNum(goldDiamondNetTreeNode.getSnapshotDate(),
                    uplinkAccountNum);
            setuplinkLevelLineAndLevel(doubleGoldDiamondUplink.getLevelLine(), doubleGoldDiamondNetTreeNode, doubleGoldDiamondUplink.getId());
            doubleGoldDiamondNetTreeNode.setUplinkId(doubleGoldDiamondUplink.getId());
            calculatePassupopv(doubleGoldDiamondUplink,doubleGoldDiamondNetTreeNode);
        } else {
            setuplinkLevelLineAndLevel(String.valueOf(0), doubleGoldDiamondNetTreeNode, 0L);
            doubleGoldDiamondNetTreeNode.setUplinkId(0);
            calculatePassupopv(null,doubleGoldDiamondNetTreeNode);
        }
    }
    // 计算余留opv
    private void calculatePassupopv(DoubleGoldDiamondNetTreeNode doubleGoldDiamondUplink,DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode){
        // 如果无双金钻上级
        if (doubleGoldDiamondUplink == null){
            doubleGoldDiamondUplink.setPassUpOpv(doubleGoldDiamondUplink.getOpv());
            return;
        }
        // 如果有双金钻上级
        // 用双金钻上级的余留opv-当前节点的opv
        doubleGoldDiamondUplink.setPassUpOpv(doubleGoldDiamondUplink.getPassUpOpv() - doubleGoldDiamondNetTreeNode.getOpv());
        doubleGoldDiamondNetTreeNodeRepository.save(doubleGoldDiamondUplink);
    }

    private void setuplinkLevelLineAndLevel(String uplinkLevelLine, DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode, Long goldDiamondUplinkId) {
        String newUplinkLevelLine = String.valueOf(new StringBuilder().append(uplinkLevelLine).append("_").append(goldDiamondUplinkId));
        String[] newUplinkLevelLines = StringUtils.split(newUplinkLevelLine, "_");
        int level = newUplinkLevelLines.length - 1;
        doubleGoldDiamondNetTreeNode.setLevelLine(newUplinkLevelLine);
        doubleGoldDiamondNetTreeNode.setLevelNum(level);
    }

    private void copyNetTree(GoldDiamondNetTreeNode goldDiamondNetTreeNode, DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode) {
        doubleGoldDiamondNetTreeNode.setSnapshotDate(goldDiamondNetTreeNode.getSnapshotDate());
        doubleGoldDiamondNetTreeNode.setData(goldDiamondNetTreeNode.getData());
        doubleGoldDiamondNetTreeNode.setReward(goldDiamondNetTreeNode.getReward());
        doubleGoldDiamondNetTreeNode.setPassUpOpv(goldDiamondNetTreeNode.getOpv());
        doubleGoldDiamondNetTreeNode.setOpv(goldDiamondNetTreeNode.getOpv());
        doubleGoldDiamondNetTreeNode.setPpv(goldDiamondNetTreeNode.getPpv());
        doubleGoldDiamondNetTreeNode.setGpv(goldDiamondNetTreeNode.getGpv());
        doubleGoldDiamondNetTreeNode.setDoubleGoldDiamondLine(0);
        doubleGoldDiamondNetTreeNodeRepository.save(doubleGoldDiamondNetTreeNode);
    }

    private Map<Long, Float> nodeOpv = new HashMap<>();
    private int upperLevelnum;
    private BigDecimal childsTotalPassupopv;
    @Override
    /**
     * Update the entire tree's DoubleGoldDiamond
     */
    public void updateWholeTreeDoubleGoldDiamond(String snapshotDate) {
        int treeLevel = getMaxTreeLevel(snapshotDate);
        if (treeLevel < 0)
            return;
        while (treeLevel > 0) {
            List<DoubleGoldDiamondNetTreeNode> thisTreeLevelTreeList = doubleGoldDiamondNetTreeNodeRepository.getTreeNodesByLevel(treeLevel);
            // 从下往上循环获取每个节点
            for (DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode : thisTreeLevelTreeList) {
                long id = doubleGoldDiamondNetTreeNode.getId();
                long accountId = doubleGoldDiamondNetTreeNode.getData().getId();
                long uplinkId = doubleGoldDiamondNetTreeNode.getUplinkId();

//                // 1、统计有多少条双金钻线
//                List<DoubleGoldDiamondNetTreeNode> childs = doubleGoldDiamondNetTreeNodeRepository.getChildNodesByUpid(id);
//                // 双金钻线的条数
//                int count = childs.size();
//                doubleGoldDiamondNetTreeNode.setDoubleGoldDiamondLine(count);
//                // 2、更新每个节点是否有下级
//                // 判断是否有子节点
//                if (count > 0) {
//                    doubleGoldDiamondNetTreeNode.setHasChild(true);
//                } else {
//                    doubleGoldDiamondNetTreeNode.setHasChild(false);
//                }
//                // 3、计算三金钻职级
//                if (count >= 7) {
//                    Account account = accountRepository.getAccountById(accountId);
//                    SavePinUtils.savePinAndHistory(account, PinPosition.TRIPLE_GOLD_DIAMOND,accountPinHistoryRepository,accountRepository);
//                }

                upperLevelnum = doubleGoldDiamondNetTreeNode.getLevelNum()+4;
                childsTotalPassupopv = new BigDecimal(0);
                // 获取当月公司opv
                OpvNetTreeNode opvNode = opvNetTreeNodeRepository.findBySnapshotDate(snapshotDate);
                Float opv = opvNode.getOpv();
                Integer length = RewardType.descOf(doubleGoldDiamondNetTreeNode.getReward()).getCode();
                BigDecimal totalRewardBonus = new BigDecimal(0);
                for (int i=1;i <= length;i++){
                    if (i == 1){
                        // 计算第一重奖励
                        // 获取自己余留opv*0.1与下4代节点的余留opv*0.1之和
                        recursiveCalculateChildsPassupopv(doubleGoldDiamondNetTreeNode);
                        totalRewardBonus = totalRewardBonus.add(calculateOnceRewardBonus(doubleGoldDiamondNetTreeNode,opv));
                        continue;
                    }
                    totalRewardBonus = totalRewardBonus.add(calculateRewardBonusHandle(i,opv));
                }
                // todo totalRewardBonus保留两位小数
                doubleGoldDiamondNetTreeNode.setRewardBonus(totalRewardBonus.setScale(2,BigDecimal.ROUND_DOWN));

                doubleGoldDiamondNetTreeNodeRepository.save(doubleGoldDiamondNetTreeNode);

            } // end for loop
            treeLevel--;
        }
    }

    private BigDecimal calculateOnceRewardBonus(DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode,Float opv){
        BigDecimal passupopv = BigDecimalUtil.multiply(doubleGoldDiamondNetTreeNode.getPassUpOpv().doubleValue(), 0.001D);
        BigDecimal average = passupopv.divide(getTotalPassupopv(), 2, BigDecimal.ROUND_DOWN);
        BigDecimal OnceRewardBonus = childsTotalPassupopv.add(average.multiply(BigDecimalUtil.multiply(0.003D,opv.doubleValue())));
        return OnceRewardBonus;
    }

    private BigDecimal getTotalPassupopv(){
        BigDecimal totalPassupopv = BigDecimalUtil.multiply(0.001D, Double.valueOf(doubleGoldDiamondNetTreeNodeRepository.sumBySnapshotDate(DateUtils.getCurrentSnapshotDate())));
        return totalPassupopv;
    }

    private BigDecimal calculateRewardBonusHandle(int type,Float opv){
        switch (type) {
            case 2:
                return calculateRewardBonus(DoubleRewardCount,opv);
            case 3:
                return calculateRewardBonus(TripleRewardCount,opv);
            case 4:
                return calculateRewardBonus(QuadrupleRewardCount,opv);
        }
        return null;
    }

    private BigDecimal calculateRewardBonus(int Count,Float opv){
        if (Count == 0){
            return new BigDecimal(0);
        }
        return BigDecimalUtil.multiply(0.001D,opv.doubleValue()).divide(new BigDecimal(Count),2,BigDecimal.ROUND_DOWN);
    }

    private BigDecimal recursiveCalculateChildsPassupopv(DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode){
        if (!(doubleGoldDiamondNetTreeNode.getLevelNum() > upperLevelnum)){
            List<DoubleGoldDiamondNetTreeNode> childs = getTreeNodeRepository().findByParentId(doubleGoldDiamondNetTreeNode.getId());
            if (childs.size() >0){
                for (DoubleGoldDiamondNetTreeNode child : childs){
                   recursiveCalculateChildsPassupopv(child);
                }
            }
        }
        BigDecimal passupopv = BigDecimalUtil.multiply(Double.valueOf(doubleGoldDiamondNetTreeNode.getPassUpOpv()), 0.001D);
        childsTotalPassupopv = childsTotalPassupopv.add(passupopv);
        return passupopv;
    }

    private void savePinAndHistory(Account account,String pin){
        account.setPin(pin);
        String maxPin = account.getMaxPin();
        Integer max = Pin.descOf(pin).getCode();
        Integer temp = Pin.descOf(maxPin).getCode();
        if (max > temp){
            account.setMaxPin(pin);
            // 五星及以上职级才保存到history表中
            if (max == Pin.descOf(PinPosition.FIVE_STAR).getCode()) temp = max-1;
            while (max > temp){
                temp+=1;
                String temp_pin = Pin.codeOf(temp).getDesc();
                AccountPinHistory accountPinHistory = new AccountPinHistory();
                accountPinHistory.setPromotionDate(new Date());
                accountPinHistory.setAccount(account);
                accountPinHistory.setCreatedBy("TerryTang");
                accountPinHistory.setLastUpdatedBy("TerryTang");
                accountPinHistory.setPin(temp_pin);
                accountPinHistoryRepository.save(accountPinHistory);
            }
        }
        accountRepository.save(account);
    }

    public TreeNode getRootNode(String snapshotDate) {
        TreeNode rootNode = doubleGoldDiamondNetTreeNodeRepository.getRootTreeNodeOfMonth(snapshotDate);
        return rootNode;
    }

    @Override
    public List<DoubleGoldDiamonndVo> convertDoubleGoldDiamondVo(String snapshotDate) {
        List<DoubleGoldDiamonndVo> doubleGoldDiamonndVos = new ArrayList<>();
        // 获取level为1的数据
        List<DoubleGoldDiamondNetTreeNode> doubleGoldDiamondNetTreeNodes = getTreeNodeRepository().getTreeNodesByLevelAndSnapshotDate(snapshotDate,1);
        if (doubleGoldDiamondNetTreeNodes.size() >0 ){
            for (DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode : doubleGoldDiamondNetTreeNodes){
                DoubleGoldDiamonndVo doubleGoldDiamonndVo = recursion(doubleGoldDiamondNetTreeNode);
                doubleGoldDiamonndVos.add(doubleGoldDiamonndVo);
            }
        }
        return doubleGoldDiamonndVos;
    }

    private DoubleGoldDiamonndVo recursion(DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode){
        List<DoubleGoldDiamondNetTreeNode> childs = getTreeNodeRepository().findByParentId(doubleGoldDiamondNetTreeNode.getId());
        List<DoubleGoldDiamonndVo> nodes = new ArrayList<>();
        if (childs != null){
            for (DoubleGoldDiamondNetTreeNode child : childs){
                DoubleGoldDiamonndVo node = recursion(child);
                nodes.add(node);
            }
        }
        return  convertChildFiveStarVo(doubleGoldDiamondNetTreeNode,nodes);
    }

    private DoubleGoldDiamonndVo convertChildFiveStarVo(DoubleGoldDiamondNetTreeNode child, List<DoubleGoldDiamonndVo> nodes){
        DoubleGoldDiamonndVo childVo = new DoubleGoldDiamonndVo();
        childVo.setLevelNum(child.getLevelNum());
        childVo.setName(child.getData().getName());
        childVo.setAccountNum(child.getData().getAccountNum());
        childVo.setPpv(child.getPpv());
        childVo.setGpv(child.getGpv());
        childVo.setOpv(child.getOpv());
        childVo.setQualifiedGoldDiamond(child.getDoubleGoldDiamondLine());
        childVo.setPin(Pin.descOf(child.getData().getPin()).getCode());
        childVo.setMaxPin(Pin.descOf(child.getData().getMaxPin()).getCode());
        childVo.setChildren(nodes);
        return childVo;
    }

}
