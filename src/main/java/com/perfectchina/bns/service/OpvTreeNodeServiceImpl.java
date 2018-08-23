package com.perfectchina.bns.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.treenode.ActiveNetTreeNode;
import com.perfectchina.bns.model.treenode.OpvNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.ActiveNetTreeNodeRepository;
import com.perfectchina.bns.repositories.OpvNetTreeNodeRepository;
import com.perfectchina.bns.service.pin.PinPosition;

@Service
public class OpvTreeNodeServiceImpl extends TreeNodeServiceImpl implements OpvTreeNodeService {

	private static final Logger logger = LoggerFactory.getLogger(OpvTreeNodeServiceImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);

	@Autowired
	private OpvNetTreeNodeRepository opvTreeNodeRepository;
	
	@Autowired
	private ActiveNetTreeNodeRepository activeNetTreeNodeRepository;
	
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
	protected ActiveNetTreeNodeRepository getTreeNodeRepository() {
		return activeNetTreeNodeRepository;
	}

	public boolean isReadyToUpdate() {
		// need to check if Simple Net already exist, otherwise, cannot
		// calculate
		boolean isReady = false;

		// Current month
		String snapshotDate = null;
		try {
			snapshotDate = sdf.format(getPreviousDateEndTime());
			ActiveNetTreeNode rootNode = activeNetTreeNodeRepository.getRootTreeNodeOfMonth(snapshotDate);
			if (rootNode != null) {
				isReady = true;
			}
		} catch (Exception ex) {
			logger.error("isReadyToUpdate, invalidDate=" + getPreviousDateEndTime());
		}
		return isReady;
	}

	public int getMaxTreeLevel(String snapShotDate) {
		return opvTreeNodeRepository.getMaxLevelNum(snapShotDate);
	}

	/**
	 * param node is SimpleNetTreeNode walking through
	 */
	protected void process(TreeNode node) {
		logger.debug("process, update node=" + node.getData().getAccountNum() + "/" + node.getData().getName()
				+ ", level [" + node.getLevelNum() + "].");

		// copy SimpleTreeNode to OpvTreeNode, inlcuding uplink Id
		ActiveNetTreeNode activeNetTreeNode = (ActiveNetTreeNode) node;
		OpvNetTreeNode opvNetTreeNode = new OpvNetTreeNode();
		//the uplinkId is SimpleNet, not OPV net
		// opvNetTreeNode.setUplinkId( simpleNetTreeNode.getUplinkId() ); 
		long uplinkId = activeNetTreeNode.getUplinkId();
		if(uplinkId!=0){
			ActiveNetTreeNode one = activeNetTreeNodeRepository.getOne(uplinkId);
			String accountNum = one.getData().getAccountNum();
			OpvNetTreeNode one2 = opvTreeNodeRepository.getAccountByAccountNum(activeNetTreeNode.getSnapshotDate(),
					accountNum);
			opvNetTreeNode.setUplinkId(one2.getId());
		}
		
		opvNetTreeNode.setHasChild(activeNetTreeNode.getHasChild());
		opvNetTreeNode.setLevelNum(activeNetTreeNode.getLevelNum());
		opvNetTreeNode.setPpv(activeNetTreeNode.getPv());
		opvNetTreeNode.setSnapshotDate(activeNetTreeNode.getSnapshotDate());
		opvNetTreeNode.setData(activeNetTreeNode.getData());

		// find out AOPV from last month of the OPVNetTreeNode for the same account
		//Long accountId = node.getData().getId();
		
		OpvNetTreeNode prevMonthNode = opvTreeNodeRepository.getAccountByAccountNum(
				DateUtils.getLastMonthSnapshotDate(activeNetTreeNode.getSnapshotDate()),
				activeNetTreeNode.getData().getAccountNum());
		
		if(prevMonthNode==null||PinPosition.MEMBER.equals(prevMonthNode.getPin())){
			opvNetTreeNode.setPin(PinPosition.MEMBER);
		}else{
			opvNetTreeNode.setPin(PinPosition.FIVE_STAR);
		}
		
		Float aopvLastMonth = 0F;
		if (prevMonthNode != null) {
			aopvLastMonth = prevMonthNode.getAopv();
		}
		opvNetTreeNode.setAopvLastMonth(aopvLastMonth);

		// retrieve child level OPV for the current month, then add current node OPV

		// throw new IllegalArgumentException("Not finished yet.");

		opvTreeNodeRepository.saveAndFlush(opvNetTreeNode);
	}

	@Override
	protected void process(TreeNode node, String snapshotDate) {

	}

	/**
	 * 更新opvNetTree 的信息并判断成为五星或推上五星
	 * @param snapshotDate
	 */
	@Override
	public void updateWholeTreeOPV(String snapshotDate) {
		int treeLevel = getMaxTreeLevel( snapshotDate );
		if (treeLevel < 0)
			return;
		
		while (treeLevel >= 0) {
			List<OpvNetTreeNode> thisTreeLevelTreeList = opvTreeNodeRepository.getTreeNodesByLevel(treeLevel);
			// loop for the children to calculate OPV at the lowest level
			for (OpvNetTreeNode opvNetTreeNode : thisTreeLevelTreeList) {
				//calculate OPV
				opvNetTreeNode.setOpv(opvNetTreeNode.getPpv());
				//List<TreeNode> childNodes = opvNetTreeNode.getChildNodes();
				List<OpvNetTreeNode> childNodes = opvTreeNodeRepository.getChildNodesByUpid(opvNetTreeNode.getId());
				for (TreeNode treeNode : childNodes) {
					OpvNetTreeNode opvChildNode = (OpvNetTreeNode) treeNode;
					Float opv = opvNetTreeNode.getOpv();
					opvNetTreeNode.setOpv(opv+opvChildNode.getOpv());
				}
				Float aopvLastMonth = opvNetTreeNode.getAopvLastMonth();
				Float opv = opvNetTreeNode.getOpv();
				//become newFiveStar
				opvNetTreeNode.setAopv(aopvLastMonth+opv);
				if(PinPosition.MEMBER.equals(opvNetTreeNode.getPin())&&opvNetTreeNode.getOpv()>=18000&&opvNetTreeNode.getAopv()>=36000){
					opvNetTreeNode.setPin(PinPosition.NEW_FIVE_STAR);
					//pass up 5star
					OpvNetTreeNode uplink = null ;
					try {
						uplink = opvTreeNodeRepository.findById(opvNetTreeNode.getUplinkId()).get();
					} catch (Exception e) {
						break;
					}
					while(uplink!=null){
						if(PinPosition.MEMBER.equals(uplink.getPin())){
							uplink.setPin(PinPosition.FIVE_STAR);
						}
						try {
							uplink = opvTreeNodeRepository.findById(uplink.getUplinkId()).get();
						} catch (Exception e) {
							break;
						}
					}
				}
				opvTreeNodeRepository.saveAndFlush(opvNetTreeNode);
			} // end for loop
			treeLevel--;
		}
	}

	public TreeNode getRootNode(String snapshotDate) {
		TreeNode rootNode = opvTreeNodeRepository.getRootTreeNodeOfMonth( snapshotDate );
		return rootNode;
	}
	
}
