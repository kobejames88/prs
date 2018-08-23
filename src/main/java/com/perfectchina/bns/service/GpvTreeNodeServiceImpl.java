package com.perfectchina.bns.service;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.treenode.GpvNetTreeNode;
import com.perfectchina.bns.model.treenode.OpvNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.GpvNetTreeNodeRepository;
import com.perfectchina.bns.repositories.OpvNetTreeNodeRepository;
import com.perfectchina.bns.service.pin.PinPosition;

@Service
public class GpvTreeNodeServiceImpl extends TreeNodeServiceImpl implements GpvTreeNodeService {

	private static final Logger logger = LoggerFactory.getLogger(GpvTreeNodeServiceImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);

	@Autowired
	private OpvNetTreeNodeRepository opvNetTreeNodeRepository;
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
	public GpvNetTreeNodeRepository getTreeNodeRepository() {
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
			OpvNetTreeNode rootNode = opvNetTreeNodeRepository.getRootTreeNodeOfMonth(snapshotDate);
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
		// Get child nodes
		TreeNode rootNode = opvNetTreeNodeRepository.getRootTreeNode(snapshotDate);
		updateChildTreeLevel( 0, rootNode ,snapshotDate);
	}
	@Override
	public void updateChildTreeLevel(Integer fromLevelNum, TreeNode fromNode,String snapshotDate) {
		super.updateChildTreeLevel(fromLevelNum, fromNode,snapshotDate);
	}
	
	public int getMaxTreeLevel(String snapShotDate) {
		return getTreeNodeRepository().getMaxLevelNum(snapShotDate);
	}

	/**
	 * param node is SimpleNetTreeNode walking through
	 */
	protected void process(TreeNode node) {
		logger.debug("process, update node=" + node.getData().getAccountNum() + "/" + node.getData().getName()
				+ ", level [" + node.getLevelNum() + "].");
		// Copy the node of the original network map plus the uplinkId to GpvNetTreeNode
		OpvNetTreeNode opvNetTreeNode = (OpvNetTreeNode) node;
		GpvNetTreeNode gpvNetTreeNode = new GpvNetTreeNode();
		//the uplinkId is SimpleNet
		long uplinkId = opvNetTreeNode.getUplinkId();
		if(uplinkId!=0){
			OpvNetTreeNode one = opvNetTreeNodeRepository.getOne(uplinkId);
			String accountNum = one.getData().getAccountNum();
			GpvNetTreeNode one2 = getTreeNodeRepository().getAccountByAccountNum(opvNetTreeNode.getSnapshotDate(),
					accountNum);
			gpvNetTreeNode.setUplinkId(one2.getId());
		}

		gpvNetTreeNode.setHasChild(opvNetTreeNode.getHasChild());
		gpvNetTreeNode.setLevelNum(opvNetTreeNode.getLevelNum());
		gpvNetTreeNode.setPpv(opvNetTreeNode.getPpv());
		gpvNetTreeNode.setOpv(opvNetTreeNode.getOpv());
		gpvNetTreeNode.setAopvLastMonth(opvNetTreeNode.getAopvLastMonth());
		gpvNetTreeNode.setAopv(opvNetTreeNode.getAopv());
		gpvNetTreeNode.setPin(opvNetTreeNode.getPin());
		gpvNetTreeNode.setSnapshotDate(opvNetTreeNode.getSnapshotDate());
		gpvNetTreeNode.setData(opvNetTreeNode.getData());


		gpvNetTreeNodeRepository.saveAndFlush(gpvNetTreeNode);
	}

	@Override
	protected void process(TreeNode node, String snapshotDate) {

	}

	@Override
	/**
	 * Update the entire tree's gpv
	 */
	public void updateWholeTreeGPV(String snapshotDate) {
		// Get the level of the original tree
		int treeLevel = getMaxTreeLevel(snapshotDate);
		if (treeLevel < 0)
			return;
		Map<Long,Float> map = new HashMap<>();
		while (treeLevel >= 0) {
			List<GpvNetTreeNode> thisTreeLevelList = gpvNetTreeNodeRepository.getTreeNodesByLevel(treeLevel);
			// loop for the children to calculate OPV at the lowest level
			for (GpvNetTreeNode gpvNetTreeNode : thisTreeLevelList) {
				long id = gpvNetTreeNode.getId();
				Float pv = gpvNetTreeNode.getPpv();
				long uplinkId = gpvNetTreeNode.getUplinkId();
                Float gpv = 0F;
				if (pv!=null){
                    Float tempPoint = map.get(id);
                    if ( tempPoint != null ){
                        gpv = pv+ tempPoint;
                    }else {
                        gpv = pv;
                    }
                }
                gpvNetTreeNode.setGpv(gpv);
				String pin = gpvNetTreeNode.getPin();
				// Determine if the member is more than the five stars
				if ( !(( StringUtils.equals(PinPosition.NEW_FIVE_STAR,pin) ) ||
						( StringUtils.equals(PinPosition.FIVE_STAR,pin) ) ||
						( StringUtils.equals(PinPosition.RUBY,pin) ) ||
						( StringUtils.equals(PinPosition.EMERALD,pin) ) ||
						( StringUtils.equals(PinPosition.DIAMOND,pin) ) ||
						( StringUtils.equals(PinPosition.GOLD_DIAMOND,pin) ) ||
						( StringUtils.equals(PinPosition.DOUBLE_GOLD_DIAMOND,pin) ) ||
						( StringUtils.equals(PinPosition.TRIPLE_GOLD_DIAMOND,pin) ))){
//                    Float gpv = gpvNetTreeNode.getGpv();
                    Float map_uplinkId = map.get(uplinkId);
                    if(map_uplinkId != null){
                        Float value = map_uplinkId;
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

	public TreeNode getRootNode(String snapshotDate) {
		TreeNode rootNode = gpvNetTreeNodeRepository.getRootTreeNodeOfMonth( snapshotDate );
		return rootNode;
	}
	
}
