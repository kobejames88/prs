package com.perfectchina.bns.service;

import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.PassUpGpv;
import com.perfectchina.bns.model.treenode.*;
import com.perfectchina.bns.repositories.*;
import com.perfectchina.bns.service.pin.PinPosition;
import com.perfectchina.bns.service.reward.RewardPosition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		updateChildTreeLevel( 0, rootNode,snapshotDate );
	}

	@Override
	public void updateChildTreeLevel(Integer fromLevelNum, TreeNode fromNode,String snapshotDate) {
		super.updateChildTreeLevel(fromLevelNum, fromNode,snapshotDate);
	}

    public int getMaxTreeLevel(String snapShotDate) {
        Integer maxLevelNum = getTreeNodeRepository().getMaxLevelNum(snapShotDate);
        return maxLevelNum == null ? -1 : maxLevelNum;
    }

	private Map<Long,Long> relation = new HashMap<>();

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
        // 获取当前元素的opv
        String accountNum = goldDiamondNetTreeNode.getData().getAccountNum();
        OpvNetTreeNode opvNetTreeNode = opvNetTreeNodeRepository.findByAccountNum(snapshotDate,accountNum);
        // 获取当前元素的所有直接下级
        List<GoldDiamondNetTreeNode> childNodes = goldDiamondNetTreeNodeRepository.getChildNodesByUpid(id);
        int count = childNodes.size();
        if (count !=0 ){
            Boolean flag = false;
            Boolean isDoubleGoldDiamond = false;
            if (count >= 7){
                // 如果有 x>=7 个直接下级，尝试合并
                Float mergingPoints = calculateMergingPoints(7, count, childNodes);
                isDoubleGoldDiamond = judgeAndSaveReward(7, mergingPoints, goldDiamondNetTreeNode, doubleGoldDiamondNetTreeNode, null,id);
                if (mergingPoints < 200000F){
                    flag = true;
                }
            }
            if (flag || (count >= 4 && count < 7)){
                // 如果有 x>=7 个直接下级但是合并失败 或 4<= x <7, 尝试合并
                Float mergingPoints = calculateMergingPoints(4, count, childNodes);
                isDoubleGoldDiamond = judgeAndSaveReward(4,mergingPoints,goldDiamondNetTreeNode,doubleGoldDiamondNetTreeNode,opvNetTreeNode,id);
            }
            // 如果不是双金钻
            for (GoldDiamondNetTreeNode childNode : childNodes){
                if (!isDoubleGoldDiamond){
                    relation.put(childNode.getId(), uplinkId);
                }else {
                    relation.put(childNode.getId(), id);
                }
            }
        }
        relation.remove(id);
	}

    private Boolean judgeAndSaveReward(int type, Float mergingPoints, GoldDiamondNetTreeNode goldDiamondNetTreeNode,
                                    DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode,OpvNetTreeNode opvNetTreeNode,
                                    Long id){
	    switch(type){
            case 7:
                if (mergingPoints >= 1000000F){
                    // 4重奖励，双金钻
                    // 判断是否有双金钻上级
                    // 设置uplinkid
                    // 将直接下级的id与自己的id存到map中
                    buildNode(doubleGoldDiamondNetTreeNode,goldDiamondNetTreeNode,RewardPosition.QUADRUPLE_REWARD,id);
                    return true;
                }
                if (mergingPoints >= 500000F){
                    // 3重奖励，双金钻
                    buildNode(doubleGoldDiamondNetTreeNode,goldDiamondNetTreeNode,RewardPosition.TRIPLE_REWARD,id);
                    return true;
                }
                if (mergingPoints >= 200000F){
                    // 2重奖励，双金钻
                    buildNode(doubleGoldDiamondNetTreeNode,goldDiamondNetTreeNode,RewardPosition.DOUBLE_REWARD,id);
                    return true;
                }
                break;
            case 4:
                // 4条100万金钻线，且本人OPV达1000万
                if (mergingPoints >= 1000000F && opvNetTreeNode.getOpv() >= 10000000F){
                    // 1重奖励，双金钻
                    buildNode(doubleGoldDiamondNetTreeNode,goldDiamondNetTreeNode,RewardPosition.ONCE_REWARD,id);
                    return true;
                }
                break;
        }
        return false;
    }

    private Float calculateMergingPoints(int line,int count,List<GoldDiamondNetTreeNode> childNodes){
        int avg = count/line;
        int mod = count%line;
        float[] every = new float[line];
        float total = 0F;
        for (int i=0;i<line;i++){
            int step = (i==line-1) ? avg+mod : avg;
            for (int j=i*avg;j<(i*avg+step);j++){
                every[i] += childNodes.get(j).getPassUpOpv();
            }
            total = (i==0) ? every[i] : every[i]+total;
        }
        return total;
    }

    private void buildNode(DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode,GoldDiamondNetTreeNode goldDiamondNetTreeNode,
                           String reward,Long id){
        doubleGoldDiamondNetTreeNode.setReward(reward);
        doubleGoldDiamondNetTreeNode.setPin(PinPosition.DOUBLE_GOLD_DIAMOND);
        setUplinkid(doubleGoldDiamondNetTreeNode,goldDiamondNetTreeNode,id);
        copyNetTree(goldDiamondNetTreeNode,doubleGoldDiamondNetTreeNode);
    }

    private void setUplinkid(DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode,GoldDiamondNetTreeNode goldDiamondNetTreeNode,Long id){
        // 获取双金钻上级的id，并通过id获取节点
        Long map_uplinkId = relation.get(id);
        if (map_uplinkId != null && map_uplinkId > 0){
            GoldDiamondNetTreeNode goldDiamondUplink = goldDiamondNetTreeNodeRepository.getOne(map_uplinkId);
            String uplinkAccountNum = goldDiamondUplink.getData().getAccountNum();
            DoubleGoldDiamondNetTreeNode doubleGoldDiamondUplink = getTreeNodeRepository().getAccountByAccountNum(goldDiamondNetTreeNode.getSnapshotDate(),
                    uplinkAccountNum);
            setuplinkLevelLineAndLevel(doubleGoldDiamondUplink.getLevelLine(),doubleGoldDiamondNetTreeNode,doubleGoldDiamondUplink.getId());
            doubleGoldDiamondNetTreeNode.setUplinkId(doubleGoldDiamondUplink.getId());
        }else {
            setuplinkLevelLineAndLevel(String.valueOf(0),doubleGoldDiamondNetTreeNode,0L);
            doubleGoldDiamondNetTreeNode.setUplinkId(0);
        }
    }

    private void setuplinkLevelLineAndLevel(String uplinkLevelLine,DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode,Long goldDiamondUplinkId){
        String newUplinkLevelLine = String.valueOf(new StringBuilder().append(uplinkLevelLine).append("_").append(goldDiamondUplinkId));
        String[] newUplinkLevelLines = StringUtils.split(newUplinkLevelLine, "_");
        int level = newUplinkLevelLines.length-1;
        doubleGoldDiamondNetTreeNode.setLevelLine(newUplinkLevelLine);
        doubleGoldDiamondNetTreeNode.setLevelNum(level);
    }

	private void copyNetTree(GoldDiamondNetTreeNode goldDiamondNetTreeNode, DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode){
        doubleGoldDiamondNetTreeNode.setSnapshotDate(goldDiamondNetTreeNode.getSnapshotDate());
        doubleGoldDiamondNetTreeNode.setData(goldDiamondNetTreeNode.getData());
		doubleGoldDiamondNetTreeNodeRepository.saveAndFlush(doubleGoldDiamondNetTreeNode);
	}

	@Override
	/**
	 * Update the entire tree's DoubleGoldDiamond
	 */
	public void updateWholeTreeDoubleGoldDiamond(String snapshotDate) {
		int treeLevel = getMaxTreeLevel(snapshotDate);
		if (treeLevel < 0)
			return;
		while (treeLevel >= 0) {
			List<DoubleGoldDiamondNetTreeNode> thisTreeLevelTreeList = doubleGoldDiamondNetTreeNodeRepository.getTreeNodesByLevel(treeLevel);
			// 从下往上循环获取每个节点
			for (DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode : thisTreeLevelTreeList) {
			    long id = doubleGoldDiamondNetTreeNode.getId();
                long accountId = doubleGoldDiamondNetTreeNode.getData().getId();
                List<GoldDiamondNetTreeNode> childs = goldDiamondNetTreeNodeRepository.getChildNodesByUpid(id);
                int counnt = childs.size();
                // 判断是否有子节点
                if (counnt>0){
                    doubleGoldDiamondNetTreeNode.setHasChild(true);
                }else {
                    doubleGoldDiamondNetTreeNode.setHasChild(false);
                }
                if (counnt >= 7){
                    changePin(doubleGoldDiamondNetTreeNode,accountId,PinPosition.TRIPLE_GOLD_DIAMOND);
                }
                doubleGoldDiamondNetTreeNodeRepository.saveAndFlush(doubleGoldDiamondNetTreeNode);
			} // end for loop
			treeLevel--;
		}
	}

	private void changePin(DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode, Long id, String pin){
        doubleGoldDiamondNetTreeNode.setPin(pin);
        Account account = accountRepository.getAccountById(id);
        account.setPin(pin);
        accountRepository.saveAndFlush(account);
    }

	public TreeNode getRootNode(String snapshotDate) {
		TreeNode rootNode = doubleGoldDiamondNetTreeNodeRepository.getRootTreeNodeOfMonth( snapshotDate );
		return rootNode;
	}

}
