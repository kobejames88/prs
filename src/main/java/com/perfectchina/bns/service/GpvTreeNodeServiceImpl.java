package com.perfectchina.bns.service;


import com.perfectchina.bns.service.pin.PinPosition;
import com.perfectchina.bns.model.treenode.GpvNetTreeNode;
import com.perfectchina.bns.model.treenode.SimpleNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.GpvNetTreeNodeRepository;
import com.perfectchina.bns.repositories.SimpleNetTreeNodeRepository;
import com.perfectchina.bns.repositories.TreeNodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GpvTreeNodeServiceImpl extends TreeNodeServiceImpl implements GpvTreeNodeService {

	private static final Logger logger = LoggerFactory.getLogger(GpvTreeNodeServiceImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);

//	@Autowired
//	private TreeNodeRepository<SimpleNetTreeNode> simpleTreeNodeRepository;
//	@Autowired
//	private TreeNodeRepository<OpvNetTreeNode> opvTreeNodeRepository;
	@Autowired
	private SimpleNetTreeNodeRepository simpleTreeNodeRepository;
	@Autowired
	private GpvNetTreeNodeRepository gpvNetTreeNodeRepository;

	
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
	public TreeNodeRepository<GpvNetTreeNode> getTreeNodeRepository() {
		return gpvNetTreeNodeRepository;
	}

	public boolean isReadyToUpdate() {
		// need to check if Simple Net already exist, otherwise, cannot
		// calculate
		boolean isReady = false;
		// // Current month
		String snapshotDate = null;
		try {
			snapshotDate = sdf.format(getPreviousDateEndTime());
			SimpleNetTreeNode rootNode = simpleTreeNodeRepository.getRootTreeNodeOfMonth(snapshotDate);
			if (rootNode != null) {
				isReady = true;
			}
		} catch (Exception ex) {
			logger.error("isReadyToUpdate, invalidDate=" + getPreviousDateEndTime());
		}
		return isReady;
	}

	/**
	 * param node is SimpleNetTreeNode walking through
	 */
	protected void process(TreeNode node) {
		logger.debug("process, update node=" + node.getData().getAccountNum() + "/" + node.getData().getName()
				+ ", level [" + node.getLevelNum() + "].");
		// Copy the node of the original network map plus the uplinkId to GpvNetTreeNode
		SimpleNetTreeNode simpleNetTreeNode = (SimpleNetTreeNode) node;
		GpvNetTreeNode gpvNetTreeNode = new GpvNetTreeNode();
		//the uplinkId is SimpleNet
		long uplinkId = simpleNetTreeNode.getUplinkId();
		if(uplinkId!=0){
			SimpleNetTreeNode one = simpleTreeNodeRepository.getOne(uplinkId);
			String accountNum = one.getData().getAccountNum();
			GpvNetTreeNode one2 = gpvNetTreeNodeRepository.getAccountByAccountNum(simpleNetTreeNode.getSnapshotDate(),
					accountNum);
			gpvNetTreeNode.setUplinkId(one2.getId());
		}

		gpvNetTreeNode.setHasChild(simpleNetTreeNode.getHasChild());
		gpvNetTreeNode.setLevelNum(simpleNetTreeNode.getLevelNum());
		gpvNetTreeNode.setPpv(simpleNetTreeNode.getPpv());
		gpvNetTreeNode.setSnapshotDate(simpleNetTreeNode.getSnapshotDate());
		gpvNetTreeNode.setData(simpleNetTreeNode.getData());

		gpvNetTreeNodeRepository.saveAndFlush(gpvNetTreeNode);
	}

	@Override
	/**
	 * Update the entire tree's gpv
	 */
	public void updateWholeTreeGPV() {
		// Get the level of the original tree
		int treeLevel = getTreeLevel();
		if (treeLevel < 0)
			return;
		Map<Long,Float> map = new HashMap<Long,Float>();
		while (treeLevel >= 0) {
			List<GpvNetTreeNode> thisTreeLevelTreeList = gpvNetTreeNodeRepository.getTreeNodesByLevel(treeLevel);
			// loop for the children to calculate OPV at the lowest level
			for (GpvNetTreeNode gpvNetTreeNode : thisTreeLevelTreeList) {
				long id = gpvNetTreeNode.getId();
				Float pv = gpvNetTreeNode.getPpv();
				long uplinkId = gpvNetTreeNode.getUplinkId();

				String pin = gpvNetTreeNode.getData().getPin();
				// Determine if the member is more than the five stars
				// Use Pin`s code to compare pin
				if ( ( pin.equals( PinPosition.NEW_FIVE_STAR)) || 
						( pin.equals( PinPosition.FIVE_STAR)) ||
						( pin.equals( PinPosition.RUBY)) ||
						( pin.equals( PinPosition.EMERALD)) ||
						( pin.equals( PinPosition.DIAMOND)) ||
						( pin.equals( PinPosition.GOLD_DIAMOND)) ||
						( pin.equals( PinPosition.DOUBLE_GOLD_DIAMOND)) ||
						( pin.equals( PinPosition.TRIPLE_GOLD_DIAMOND)) 
						){
					gpvNetTreeNode.setGpv(pv);
				}else {
					// Determine if the id is in the key of the map
					Float tempPoint = map.get(id);
					if ( tempPoint != null ){
						gpvNetTreeNode.setGpv(pv+ tempPoint);
					}else {
						gpvNetTreeNode.setGpv(pv);
					}
					Float gpv = gpvNetTreeNode.getGpv();
					if(map.containsKey(uplinkId)){
						Float value = map.get(uplinkId);
						Float newVal = value+gpv;
						map.put(uplinkId,newVal);
					}else {
						map.put(uplinkId,gpv);
					}
				}
				gpvNetTreeNodeRepository.saveAndFlush(gpvNetTreeNode);
			} // end for loop
			treeLevel--;
		}
	}

	/**
	 * Get the level of the original tree
	 * @return
	 */
	private int getTreeLevel() {
		// get root node
		TreeNode fromNode = simpleTreeNodeRepository.getRootTreeNode();
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
