package com.perfectchina.bns.service;

import com.perfectchina.bns.common.utils.BigDecimalUtil;
import com.perfectchina.bns.common.utils.SavePinUtils;
import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.AccountPinHistory;
import com.perfectchina.bns.model.PassUpGpv;
import com.perfectchina.bns.model.treenode.*;
import com.perfectchina.bns.model.vo.DoubleGoldDiamonndVo;
import com.perfectchina.bns.model.vo.GoldDiamonndVo;
import com.perfectchina.bns.repositories.*;
import com.perfectchina.bns.service.Enum.Pin;
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
                    buildNode(doubleGoldDiamondNetTreeNode, goldDiamondNetTreeNode, id);
                }
            }
        }

        relation.remove(id);
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
        } else {
            setuplinkLevelLineAndLevel(String.valueOf(0), doubleGoldDiamondNetTreeNode, 0L);
            doubleGoldDiamondNetTreeNode.setUplinkId(0);
        }
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
//                // 1、计算余留opv
//                Float childOpv = nodeOpv.get(id);
//                if (childOpv != null){
//                    doubleGoldDiamondNetTreeNode.setPassUpOpv(doubleGoldDiamondNetTreeNode.getPassUpOpv() - childOpv);
//                }
//                if (uplinkId > 0){
//                    nodeOpv.put(uplinkId,doubleGoldDiamondNetTreeNode.getOpv());
//                }

                upperLevelnum = doubleGoldDiamondNetTreeNode.getLevelNum()+4;
                childsTotalPassupopv = new BigDecimal(0);
                recursiveCalculateChildsPassupopv(doubleGoldDiamondNetTreeNode);

//                // 2、统计有多少条双金钻线
//                List<DoubleGoldDiamondNetTreeNode> childs = doubleGoldDiamondNetTreeNodeRepository.getChildNodesByUpid(id);
//                // 双金钻线的条数
//                int count = childs.size();
//                doubleGoldDiamondNetTreeNode.setDoubleGoldDiamondLine(count);
//                // 3、更新每个节点是否有下级
//                // 判断是否有子节点
//                if (count > 0) {
//                    doubleGoldDiamondNetTreeNode.setHasChild(true);
//                } else {
//                    doubleGoldDiamondNetTreeNode.setHasChild(false);
//                }
//                // 4、计算三金钻职级
//                if (count >= 7) {
//                    Account account = accountRepository.getAccountById(accountId);
//                    SavePinUtils.savePinAndHistory(account, PinPosition.TRIPLE_GOLD_DIAMOND,accountPinHistoryRepository,accountRepository);
//                }
//                doubleGoldDiamondNetTreeNodeRepository.save(doubleGoldDiamondNetTreeNode);

            } // end for loop
            treeLevel--;
        }
    }

    private BigDecimal recursiveCalculateChildsPassupopv(DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode){
        if (!(doubleGoldDiamondNetTreeNode.getLevelNum() > upperLevelnum)){
            List<DoubleGoldDiamondNetTreeNode> childs = getTreeNodeRepository().findByParentId(doubleGoldDiamondNetTreeNode.getId());
            if (childs.size() >0){
                for (DoubleGoldDiamondNetTreeNode child : childs){
                    BigDecimal childsPassupopv = recursiveCalculateChildsPassupopv(child);
//                    childsTotalPassupopv = childsTotalPassupopv.add(childsPassupopv);
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
