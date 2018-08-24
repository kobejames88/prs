package com.perfectchina.bns.service;

import com.perfectchina.bns.model.PassUpGpv;
import com.perfectchina.bns.model.treenode.*;
import com.perfectchina.bns.repositories.*;
import com.perfectchina.bns.service.pin.PinPosition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GoldDiamondTreeNodeServiceImpl extends TreeNodeServiceImpl implements GoldDiamondTreeNodeService {

	private static final Logger logger = LoggerFactory.getLogger(GoldDiamondTreeNodeServiceImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);

	@Autowired
	private GoldDiamondNetTreeNodeRepository goldDiamondNetTreeNodeRepository;

	@Autowired
	private QualifiedFiveStarNetTreeNodeRepository qualifiedFiveStarNetTreeNodeRepository;

	@Autowired
    private OpvNetTreeNodeRepository opvNetTreeNodeRepository;

	@Autowired
	private SimpleNetTreeNodeRepository simpleNetTreeNodeRepository;

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
	public TreeNodeRepository<GoldDiamondNetTreeNode> getTreeNodeRepository() {
		return goldDiamondNetTreeNodeRepository;
	}

	public boolean isReadyToUpdate() {
		// need to check if Simple Net already exist, otherwise, cannot
		// calculate
		boolean isReady = false;
		// // Current month
		String snapshotDate = null;
		try {
			snapshotDate = sdf.format(getPreviousDateEndTime());
			QualifiedFiveStarNetTreeNode rootNode = qualifiedFiveStarNetTreeNodeRepository.getRootTreeNodeOfMonth(snapshotDate);

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
		TreeNode rootNode = qualifiedFiveStarNetTreeNodeRepository.getRootTreeNode(snapshotDate);
		updateChildTreeLevel( 0, rootNode,snapshotDate );
	}

	@Override
	public void updateChildTreeLevel(Integer fromLevelNum, TreeNode fromNode,String snapshotDate) {
		super.updateChildTreeLevel(fromLevelNum, fromNode,snapshotDate);
	}

    public int getMaxTreeLevel(String snapShotDate) {
        Integer maxLevelNum = getTreeNodeRepository().getMaxLevelNum(snapShotDate);
        return maxLevelNum == null ? 0 : maxLevelNum;
    }

	private Map<Long,Long> relation = new HashMap<>();

	@Override
	protected void process(TreeNode node, String snapshotDate) {
		logger.debug("process, update node=" + node.getData().getAccountNum() + "/" + node.getData().getName()
				+ ", level [" + node.getLevelNum() + "].");
		// 当前元素
		QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode = (QualifiedFiveStarNetTreeNode) node;
		// 待装载元素
		GoldDiamondNetTreeNode goldDiamondNetTreeNode = new GoldDiamondNetTreeNode();
		String pin = qualifiedFiveStarNetTreeNode.getPin();
		Long id = qualifiedFiveStarNetTreeNode.getId();
		Long map_uplinkId = relation.get(id);

		String accountNum = qualifiedFiveStarNetTreeNode.getData().getAccountNum();
		OpvNetTreeNode opvNetTreeNode = opvNetTreeNodeRepository.findByAccountNum(snapshotDate,accountNum);
		SimpleNetTreeNode simpleNetTreeNode = simpleNetTreeNodeRepository.findByAccountNum(snapshotDate, accountNum);


		if (map_uplinkId != null){
			QualifiedFiveStarNetTreeNode qualifiedFiveStarUplink = qualifiedFiveStarNetTreeNodeRepository.getOne(map_uplinkId);
			String uplinkAccountNum = qualifiedFiveStarUplink.getData().getAccountNum();
			GoldDiamondNetTreeNode goldDiamondUplink = getTreeNodeRepository().getAccountByAccountNum(qualifiedFiveStarNetTreeNode.getSnapshotDate(),
					uplinkAccountNum);
			long qualifiedFiveStarUplinkId = goldDiamondUplink.getId();
			String uplinkLevelLine = goldDiamondUplink.getLevelLine();

			if (StringUtils.equals(PinPosition.GOLD_DIAMOND,pin)){
				goldDiamondNetTreeNode.setUplinkId(goldDiamondUplink.getId());
				goldDiamondNetTreeNode.setOpv(opvNetTreeNode.getOpv());
				goldDiamondNetTreeNode.setPassUpOpv(simpleNetTreeNode.getPpv());
				// 获取金钻的所有直接下级
				List<QualifiedFiveStarNetTreeNode> childNodes = qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(id);
				for (TreeNode childNode : childNodes){
					QualifiedFiveStarNetTreeNode qualifiedFiveChildNode = (QualifiedFiveStarNetTreeNode)childNode;
					relation.put(qualifiedFiveChildNode.getId(),qualifiedFiveStarNetTreeNode.getId());
				}
				setuplinkLevelLineAndLevel(uplinkLevelLine,goldDiamondNetTreeNode,qualifiedFiveStarUplinkId);
				copyNetTree(qualifiedFiveStarNetTreeNode,goldDiamondNetTreeNode);
			}else {
				goldDiamondUplink.setPassUpOpv(goldDiamondUplink.getPassUpOpv()+simpleNetTreeNode.getPpv());
				// 获取当前元素的所有直接下级
				List<TreeNode> childNodes = qualifiedFiveStarNetTreeNode.getChildNodes();
				for (TreeNode childNode : childNodes){
					QualifiedFiveStarNetTreeNode qualifiedFiveChildNode = (QualifiedFiveStarNetTreeNode)childNode;
					relation.put(qualifiedFiveChildNode.getId(),map_uplinkId);
				}
				goldDiamondNetTreeNodeRepository.saveAndFlush(goldDiamondUplink);
				relation.remove(id);
			}
		}else {
			if (StringUtils.equals(PinPosition.GOLD_DIAMOND,pin)){
				goldDiamondNetTreeNode.setUplinkId(0);
				goldDiamondNetTreeNode.setOpv(opvNetTreeNode.getOpv());
				goldDiamondNetTreeNode.setPassUpOpv(simpleNetTreeNode.getPpv());
				goldDiamondNetTreeNode.setLevelLine(String.valueOf(0));
				goldDiamondNetTreeNode.setLevelNum(0);
				// 获取金钻的所有直接下级
				List<QualifiedFiveStarNetTreeNode> childNodes = qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(id);
				for (TreeNode childNode : childNodes){
					QualifiedFiveStarNetTreeNode qualifiedFiveChildNode = (QualifiedFiveStarNetTreeNode)childNode;
					relation.put(qualifiedFiveChildNode.getId(),qualifiedFiveStarNetTreeNode.getId());
				}
				copyNetTree(qualifiedFiveStarNetTreeNode,goldDiamondNetTreeNode);
			}
		}
	}

	private void setuplinkLevelLineAndLevel(String uplinkLevelLine,GoldDiamondNetTreeNode goldDiamondNetTreeNode,Long goldDiamondUplinkId){
        String newUplinkLevelLine = String.valueOf(new StringBuilder().append(uplinkLevelLine).append("_").append(goldDiamondUplinkId));
        String[] newUplinkLevelLines = StringUtils.split(newUplinkLevelLine, "_");
        int level = newUplinkLevelLines.length-1;
        goldDiamondNetTreeNode.setLevelLine(newUplinkLevelLine);
        goldDiamondNetTreeNode.setLevelNum(level);
    }

	private void copyNetTree(QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode,GoldDiamondNetTreeNode goldDiamondNetTreeNode){
        goldDiamondNetTreeNode.setSnapshotDate(qualifiedFiveStarNetTreeNode.getSnapshotDate());
        goldDiamondNetTreeNode.setData(qualifiedFiveStarNetTreeNode.getData());
		goldDiamondNetTreeNodeRepository.saveAndFlush(goldDiamondNetTreeNode);
	}

	@Override
	/**
	 * Update the entire tree's pass-up-gpv
	 */
	public void updateWholeTreeGoldDiamond(String snapshotDate) {
		// Get the level of the original tree
		int treeLevel = getMaxTreeLevel(snapshotDate);
		if (treeLevel < 0)
			return;
		while (treeLevel > 0) {
			List<GoldDiamondNetTreeNode> thisTreeLevelTreeList = goldDiamondNetTreeNodeRepository.getTreeNodesByLevel(treeLevel);
			// loop for the children to calculate OPV at the lowest level
			for (GoldDiamondNetTreeNode goldDiamondNetTreeNode : thisTreeLevelTreeList) {
                long id = goldDiamondNetTreeNode.getId();
                List<GoldDiamondNetTreeNode> childs = goldDiamondNetTreeNodeRepository.getChildNodesByUpid(id);
                if (childs.size()>0){
                    goldDiamondNetTreeNode.setHasChild(true);
                }else {
                    goldDiamondNetTreeNode.setHasChild(false);
                }
                goldDiamondNetTreeNodeRepository.saveAndFlush(goldDiamondNetTreeNode);
			} // end for loop
			treeLevel--;
		}
	}

	public TreeNode getRootNode(String snapshotDate) {
		TreeNode rootNode = goldDiamondNetTreeNodeRepository.getRootTreeNodeOfMonth( snapshotDate );
		return rootNode;
	}

}
