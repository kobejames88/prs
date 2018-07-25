package com.perfectchina.bns.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perfectchina.bns.model.treenode.OpvNetTreeNode;
import com.perfectchina.bns.model.treenode.SimpleNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.OpvNetTreeNodeRepository;
import com.perfectchina.bns.repositories.SimpleNetTreeNodeRepository;
import com.perfectchina.bns.repositories.TreeNodeRepository;

@Service
public class OpvTreeNodeServiceImpl extends TreeNodeServiceImpl implements OpvTreeNodeService {

	private static final Logger logger = LoggerFactory.getLogger(OpvTreeNodeServiceImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);

//	@Autowired
//	private TreeNodeRepository<SimpleNetTreeNode> simpleTreeNodeRepository;
//	@Autowired
//	private TreeNodeRepository<OpvNetTreeNode> opvTreeNodeRepository;
	@Autowired
	private SimpleNetTreeNodeRepository simpleTreeNodeRepository;
	@Autowired
	private OpvNetTreeNodeRepository opvTreeNodeRepository;

	
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
	public TreeNodeRepository<OpvNetTreeNode> getTreeNodeRepository() {
		return opvTreeNodeRepository;
	}

	public boolean isReadyToUpdate() {
		// need to check if Simple Net already exist, otherwise, cannot
		// calculate
		boolean isReady = false;
		// 当前月份
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

		// copy SimpleTreeNode to OpvTreeNode, inlcuding uplink Id
		// 将原始网络图的节点加上上级id复制到OpvTreeNode
		SimpleNetTreeNode simpleNetTreeNode = (SimpleNetTreeNode) node;
		OpvNetTreeNode opvNetTreeNode = new OpvNetTreeNode();
		//the uplinkId is SimpleNet, not OPV net
		// opvNetTreeNode.setUplinkId( simpleNetTreeNode.getUplinkId() ); 
		long uplinkId = simpleNetTreeNode.getUplinkId();
		if(uplinkId!=0){
			SimpleNetTreeNode one = simpleTreeNodeRepository.getOne(uplinkId);
			String accountNum = one.getData().getAccountNum();
			OpvNetTreeNode one2 = opvTreeNodeRepository.getAccountByAccountNum(simpleNetTreeNode.getSnapshotDate(),
					accountNum);
			opvNetTreeNode.setUplinkId(one2.getId());
		}
		
		opvNetTreeNode.setHasChild(simpleNetTreeNode.getHasChild());
		opvNetTreeNode.setLevelNum(simpleNetTreeNode.getLevelNum());
		opvNetTreeNode.setPpv(simpleNetTreeNode.getPpv());
		opvNetTreeNode.setSnapshotDate(simpleNetTreeNode.getSnapshotDate());
		opvNetTreeNode.setData(simpleNetTreeNode.getData());

		// find out AOPV from last month of the OPVNetTreeNode for the same account
		Long accountId = node.getData().getId();
		
		OpvNetTreeNode prevMonthNode = opvTreeNodeRepository.getAccountByAccountNum(simpleNetTreeNode.getSnapshotDate(),
				simpleNetTreeNode.getData().getAccountNum());
		
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
	public void updateWholeTreeOPV() {
		int treeLevel = getTreeLevel();
		if (treeLevel < 0)
			return;
		
		while (treeLevel >= 0) {
			List<OpvNetTreeNode> thisTreeLevelTreeList = opvTreeNodeRepository.getTreeNodesByLevel(treeLevel);
			// loop for the children to calculate OPV at the lowest level
			for (OpvNetTreeNode opvNetTreeNode : thisTreeLevelTreeList) {
				//calculate OPV
				opvNetTreeNode.setOpv(opvNetTreeNode.getPpv());
				//List<TreeNode> childNodes = opvNetTreeNode.getChildNodes();
				List<TreeNode> childNodes = opvTreeNodeRepository.getChildNodesByUpid(opvNetTreeNode.getId());
				for (TreeNode treeNode : childNodes) {
					OpvNetTreeNode opvChildNode = (OpvNetTreeNode) treeNode;
					opvNetTreeNode.setOpv(opvNetTreeNode.getOpv()+opvChildNode.getOpv());
				}
				opvTreeNodeRepository.saveAndFlush(opvNetTreeNode);
			} // end for loop
			treeLevel--;
		}
	}

	/**
	 * 获取树的等级
	 * @return
	 */
	private int getTreeLevel() {
		// get root node
		TreeNode fromNode = simpleTreeNodeRepository.getRootTreeNode();
		int treeLevel = 0;
		
		Stack<TreeNode> stk = new Stack<TreeNode>();
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
