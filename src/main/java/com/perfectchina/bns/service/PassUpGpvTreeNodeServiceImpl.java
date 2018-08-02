package com.perfectchina.bns.service;


import com.perfectchina.bns.model.treenode.*;
import com.perfectchina.bns.repositories.*;
import com.perfectchina.bns.service.pin.PinPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PassUpGpvTreeNodeServiceImpl extends TreeNodeServiceImpl implements PassUpGpvTreeNodeService {

	private static final Logger logger = LoggerFactory.getLogger(PassUpGpvTreeNodeServiceImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);

	@Autowired
	private FiveStarNetTreeNodeRepository fiveStarNetTreeNodeRepository;
	@Autowired
	private PassUpGpvNetTreeNodeRepository passUpGpvNetTreeNodeRepository;

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
	public TreeNodeRepository<FiveStarNetTreeNode> getTreeNodeRepository() {
		return fiveStarNetTreeNodeRepository;
	}

	public boolean isReadyToUpdate() {
		// need to check if Simple Net already exist, otherwise, cannot
		// calculate
		boolean isReady = false;
		// // Current month
		String snapshotDate = null;
		try {
			snapshotDate = sdf.format(getPreviousDateEndTime());
			FiveStarNetTreeNode rootNode = fiveStarNetTreeNodeRepository.getRootTreeNodeOfMonth(snapshotDate);

			if (rootNode != null) {
				isReady = true;
			}
		} catch (Exception ex) {
			logger.error("isReadyToUpdate, invalidDate=" + getPreviousDateEndTime());
		}
		return isReady;
	}

	@Override
	public void updateWholeTree() {
		TreeNode rootNode = getTreeNodeRepository().getRootTreeNode();
		updateChildTreeLevel( 0, rootNode );
	}

	@Override
	public void updateChildTreeLevel(Integer fromLevelNum, TreeNode fromNode) {
		super.updateChildTreeLevel(fromLevelNum, fromNode);
	}

	@Override
	public int getMaxTreeLevel() {
        int maxLevelNum = getTreeNodeRepository().getMaxLevelNum();
        return maxLevelNum;
	}

	/**
	 * param node is SimpleNetTreeNode walking through
	 */
	protected void process(TreeNode node) {
		logger.debug("process, update node=" + node.getData().getAccountNum() + "/" + node.getData().getName()
				+ ", level [" + node.getLevelNum() + "].");
		// Copy the node of the original network map plus the uplinkId to GpvNetTreeNode
		FiveStarNetTreeNode fiveStarNetTreeNode = (FiveStarNetTreeNode) node;
		PassUpGpvNetTreeNode passUpGpvNetTreeNode = new PassUpGpvNetTreeNode();
		//the uplinkId is SimpleNet
		long uplinkId = fiveStarNetTreeNode.getUplinkId();
		if(uplinkId!=0){
			FiveStarNetTreeNode one = getTreeNodeRepository().getOne(uplinkId);
			String accountNum = one.getData().getAccountNum();
			PassUpGpvNetTreeNode one2 = passUpGpvNetTreeNodeRepository.getAccountByAccountNum(fiveStarNetTreeNode.getSnapshotDate(),
					accountNum);
			passUpGpvNetTreeNode.setUplinkId(one2.getId());
		}

		passUpGpvNetTreeNode.setHasChild(fiveStarNetTreeNode.getHasChild());
		passUpGpvNetTreeNode.setLevelNum(fiveStarNetTreeNode.getLevelNum());
		passUpGpvNetTreeNode.setGpv(fiveStarNetTreeNode.getGpv());
		passUpGpvNetTreeNode.setSnapshotDate(fiveStarNetTreeNode.getSnapshotDate());
		passUpGpvNetTreeNode.setData(fiveStarNetTreeNode.getData());

		passUpGpvNetTreeNodeRepository.saveAndFlush(passUpGpvNetTreeNode);
	}

	@Override
	/**
	 * Update the entire tree's pass-up-gpv
	 */
	public void updateWholeTreePassUpGPV() {
		// Get the level of the original tree
		int treeLevel = passUpGpvNetTreeNodeRepository.getMaxLevelNum();
//		int treeLevel = getTreeLevel();
		if (treeLevel < 0)
			return;
		Map<Long,Float> map = new HashMap<>();
		while (treeLevel >= 0) {
			List<PassUpGpvNetTreeNode> thisTreeLevelTreeList = passUpGpvNetTreeNodeRepository.getTreeNodesByLevel(treeLevel);
			// loop for the children to calculate OPV at the lowest level
			for (PassUpGpvNetTreeNode passUpGpvNetTreeNode : thisTreeLevelTreeList) {
				long id = passUpGpvNetTreeNode.getId();
				long uplinkId = passUpGpvNetTreeNode.getUplinkId();
				Float gpv = passUpGpvNetTreeNode.getGpv();
				Float tempPoint = map.get(id);
				if ( tempPoint != null ){
					passUpGpvNetTreeNode.setPassUpGpv(gpv+ tempPoint);
				}else {
					passUpGpvNetTreeNode.setPassUpGpv(gpv);
				}
				Float passUpGpv = passUpGpvNetTreeNode.getPassUpGpv();
				int qualifiedLine = passUpGpvNetTreeNode.getQualifiedLine();
				List<PassUpGpvNetTreeNode> nodes = new ArrayList<>();
				if(uplinkId != 0){
					PassUpGpvNetTreeNode upLinkNode = passUpGpvNetTreeNodeRepository.getOne(uplinkId);
					if (passUpGpv >= 18000F){
						upLinkNode.setQualifiedLine(upLinkNode.getQualifiedLine()+1);
					}else {
						if (isAboveRuby(passUpGpv,qualifiedLine)){
							upLinkNode.setQualifiedLine(upLinkNode.getQualifiedLine()+1);
						}else {
							Float mapUplinkId = map.get(uplinkId);
							if(mapUplinkId != null){
								Float value = mapUplinkId;
								Float newVal = value+passUpGpv;
								map.put(uplinkId,newVal);
							}else {
								map.put(uplinkId,passUpGpv);
							}
						}
					}
					nodes.add(upLinkNode);
				}
				nodes.add(passUpGpvNetTreeNode);
				passUpGpvNetTreeNodeRepository.save(nodes);
				passUpGpvNetTreeNodeRepository.flush();
			} // end for loop
			treeLevel--;
		}
	}

	private Boolean isAboveRuby(Float passUpGpv,int qualifiedLine){
		if ((passUpGpv >= 9000F && qualifiedLine > 0) || (qualifiedLine >= 2)){
			return true;
		}
		return false;
	}

	/**
	 * Get the level of the five-star-net-tree
	 * @return
	 */
	private int getTreeLevel() {
		// get root node
		TreeNode fromNode = fiveStarNetTreeNodeRepository.getRootTreeNode();
		int treeLevel = 0;
		
		Stack<TreeNode> stk = new Stack<>();
		stk.push(fromNode);
		while (!stk.empty()) {
			TreeNode top = stk.pop();
			for (TreeNode child : top.getChildNodes()) {
				treeLevel = treeLevel > child.getLevelNum() ? treeLevel : child.getLevelNum();
				stk.push(child);
			}
		}
		return treeLevel;
	}

}
