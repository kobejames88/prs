package com.perfectchina.bns.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import javax.transaction.Transactional;

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
import com.perfectchina.bns.repositories.TreeNodeRepository;

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

	@Override
	public int getMaxTreeLevel() {
        return 0;
	}

    @Override
	public SimpleNetTreeNodeRepository getTreeNodeRepository() {
		return simpleTreeNodeRepository;
	}

	@Override
	protected void process(TreeNode node) {
		// TODO Auto-generated method stub
		logger.debug("process, update node=" + node.getData().getAccountNum() + "/" + node.getData().getName()
				+ ", level [" + node.getLevelNum() + "].");
	}
	
	/**
	 * base on simple net tree
	 */
	@Override
	public void createActiveNetTree() {
		//first copy value from simplenettree
		SimpleNetTreeNode rootNode = simpleTreeNodeRepository.getRootTreeNodeOfMonth(DateUtils.getCurrentSnapshotDate());
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
			activeNetTreeNode.setIsActiveMember(top.getPpv()>=200);
			//gpv temporary value gpv = ppv;conveinet for calculate gpv
			activeNetTreeNode.setGpv(top.getPpv());
			//find and set upLinkId
			if(top.getUplinkId()!=0){
				SimpleNetTreeNode upLinkNode = simpleTreeNodeRepository.getOne(top.getUplinkId());
				String accountNum = upLinkNode.getData().getAccountNum();
				ActiveNetTreeNode upLinkNode2 = activeNetTreeNodeRepository.getAccountByAccountNum(top.getSnapshotDate(), accountNum);
				activeNetTreeNode.setUplinkId(upLinkNode2.getId());
			}
			else{
				//root
				activeNetTreeNode.setIsActiveMember(true);
			}
			activeNetTreeNodeRepository.saveAndFlush(activeNetTreeNode);
		}
		//then build activeNetTree
		updateWholeTreeActiveNet();
	}
	
	
	public void updateWholeTreeActiveNet() {
		//find all snapshot date leafNodes;from leadNode climb up
		List<ActiveNetTreeNode> leafNodes = activeNetTreeNodeRepository.findLeafNodes(DateUtils.getCurrentSnapshotDate());
		for (ActiveNetTreeNode activeNetTreeNode : leafNodes) {
			ActiveNetTreeNode upLinkNode = activeNetTreeNodeRepository.getOne(activeNetTreeNode.getUplinkId());
			//climb up  until root
			while(upLinkNode!=null){
				//if active find uplink node until the node is active or root;then set upLinkId
				if(activeNetTreeNode.getIsActiveMember()){
					ActiveNetTreeNode upLinkNode2 = upLinkNode;
					while(!upLinkNode2.getIsActiveMember()){
						upLinkNode2 = activeNetTreeNodeRepository.findOne(upLinkNode2.getUplinkId());
						if(upLinkNode2==null){break;}
						activeNetTreeNode.setUplinkId(upLinkNode2.getId());
					}
				//pass gpv to uplinkNode;then set gpv=0,avoid repeatb calculate gpv
				}else{
					upLinkNode.setGpv(upLinkNode.getGpv()+activeNetTreeNode.getGpv());
					activeNetTreeNode.setGpv(0);
				}
				//loop
				activeNetTreeNode = upLinkNode;
				upLinkNode = activeNetTreeNodeRepository.findOne(activeNetTreeNode.getUplinkId());
			}
		}
		//delete no active member
		activeNetTreeNodeRepository.deleteNOActiveMember();
		updataLevel();
	  }

	private void updataLevel() {
		int fromLevelNum = 0;
		ActiveNetTreeNode rootTreeNode = activeNetTreeNodeRepository.getRootTreeNodeOfMonth(DateUtils.getCurrentSnapshotDate());
		
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

}
