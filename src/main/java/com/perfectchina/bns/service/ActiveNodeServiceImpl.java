package com.perfectchina.bns.service;

import java.text.SimpleDateFormat;
import java.util.*;

import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.treenode.ActiveNetTreeNode;
import com.perfectchina.bns.model.treenode.SimpleNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.ActiveNetTreeNodeRepository;
import com.perfectchina.bns.repositories.SimpleNetTreeNodeRepository;

import javax.swing.text.html.Option;

@Service
public class ActiveNodeServiceImpl extends TreeNodeServiceImpl implements ActiveNodeService {

	private static final Logger logger = LoggerFactory.getLogger(ActiveNodeServiceImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);

	@Autowired
	private ActiveNetTreeNodeRepository activeNetTreeNodeRepository;
	@Autowired
	private SimpleNetTreeNodeRepository simpleTreeNodeRepository;

	// Parameter to set calculate PPV for
	// which month
	private Date previousDateEndTime;

	public Date getPreviousDateEndTime() {
		return previousDateEndTime;
	}
	public void setPreviousDateEndTime(Date previousDateEndTime) {
		this.previousDateEndTime = previousDateEndTime;
	}

	@Override
	public boolean isReadyToUpdate() {
		// need to check if Simple Net already exist, otherwise, cannot
		// Check current month tree to see the node exist
		// calculate
		boolean isReady = false;
		// Current month
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

	public int getMaxTreeLevel(String snapShotDate) {
		return activeNetTreeNodeRepository.getMaxLevelNum(snapShotDate);
	}

    @Override
	public SimpleNetTreeNodeRepository getTreeNodeRepository() {
		return simpleTreeNodeRepository;
	}

	@Override
	protected void process(TreeNode node, String snapshotDate) {
		// TODO Auto-generated method stub
		logger.debug("process, update node=" + node.getData().getAccountNum() + "/" + node.getData().getName()
				+ ", level [" + node.getLevelNum() + "].");
	}


	/**
	 * base on simple net tree
	 */
	@Override
	public void createActiveNetTree(String snapShotDate) {
		//first copy value from simplenettree ; find rootNode by lastMonth snapshotDate
		SimpleNetTreeNode rootNode = simpleTreeNodeRepository.getRootTreeNodeOfMonth(snapShotDate);
		Stack<SimpleNetTreeNode> stk = new Stack<SimpleNetTreeNode>();
		stk.push(rootNode);
		while(!stk.isEmpty()){
			SimpleNetTreeNode top = stk.pop();
			for(TreeNode child :top.getChildNodes()){
				stk.push((SimpleNetTreeNode) child);
			}
			ActiveNetTreeNode activeNetTreeNode = new ActiveNetTreeNode();
			activeNetTreeNode.setHasChild(top.getHasChild());
			activeNetTreeNode.setLevelNum(top.getLevelNum());
			activeNetTreeNode.setSnapshotDate(top.getSnapshotDate());
			activeNetTreeNode.setData(top.getData());
			activeNetTreeNode.setPv(top.getPpv());
			//find and set upLinkId
			if(top.getUplinkId()!=0){
				SimpleNetTreeNode upLinkNode = simpleTreeNodeRepository.getOne(top.getUplinkId());
				String accountNum = upLinkNode.getData().getAccountNum();
				ActiveNetTreeNode upLinkNode2 = activeNetTreeNodeRepository.getAccountByAccountNum(top.getSnapshotDate(), accountNum);
				activeNetTreeNode.setUplinkId(upLinkNode2.getId());
			}
			activeNetTreeNodeRepository.saveAndFlush(activeNetTreeNode);
		}
		//then build activeNetTree
		updateWholeTreeActiveNet(snapShotDate);
	}


	public void updateWholeTreeActiveNet(String snapShotDate) {
		//find all last month snapshot date leafNodes;from leadNode climb up
		List<ActiveNetTreeNode> leafNodes = activeNetTreeNodeRepository.findLeafNodes(snapShotDate);
		for (ActiveNetTreeNode activeNetTreeNode : leafNodes) {
			ActiveNetTreeNode upLinkNode = activeNetTreeNodeRepository.getOne(activeNetTreeNode.getUplinkId());
			//climb up  until root
			while(upLinkNode!=null){
					ActiveNetTreeNode upLinkNode2 = upLinkNode;
					//find upLink until upLink is active
					while(upLinkNode2.getPv()<200){
						ActiveNetTreeNode upLinkNode2Up = null;
						try {
							upLinkNode2Up = activeNetTreeNodeRepository.findById(upLinkNode2.getUplinkId()).get();
						} catch (Exception e) {
							break;
						}
						if(upLinkNode2Up == null){break;}
						upLinkNode2Up.setPv(upLinkNode2Up.getPv()+upLinkNode2.getPv());
						upLinkNode2.setPv(0F);
						upLinkNode2 = upLinkNode2Up;
						activeNetTreeNode.setUplinkId(upLinkNode2.getId());
					}
				//loop
				activeNetTreeNode = upLinkNode;
				try {
					upLinkNode = activeNetTreeNodeRepository.findById(activeNetTreeNode.getUplinkId()).get();
					if(upLinkNode!=null&&activeNetTreeNode.getPv()< 200){
						upLinkNode.setPv(upLinkNode.getPv()+activeNetTreeNode.getPv());
						activeNetTreeNode.setPv(0);
					}
				} catch (Exception e) {
					break;
				}
			}
		}
		//delete no active member
		activeNetTreeNodeRepository.deleteNOActiveMember();
		updataLevel(snapShotDate);
	}

	private void updataLevel(String snapShotDate) {
		int fromLevelNum = 0;
		ActiveNetTreeNode rootTreeNode = activeNetTreeNodeRepository.getRootTreeNodeOfMonth(snapShotDate);
	    Stack<TreeNode> stk = new Stack<TreeNode>();
	    rootTreeNode.setLevelNum(fromLevelNum);	    
	    stk.push(rootTreeNode);

	    while (!stk.empty()) {
	        TreeNode top = stk.pop();
	        int childLevelNum = top.getLevelNum() + 1;
	        for ( TreeNode child : activeNetTreeNodeRepository.getChildNodesByUpid(top.getId())) {
	        	child.setLevelNum( childLevelNum );
	            stk.push(child);
	        }	        
	    }
	    activeNetTreeNodeRepository.flush();
	}

	public TreeNode getRootNode(String snapshotDate) {
		TreeNode rootNode = activeNetTreeNodeRepository.getRootTreeNodeOfMonth( snapshotDate );
		return rootNode;
	}
}
