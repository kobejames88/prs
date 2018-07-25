package com.perfectchina.bns.service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.service.pin.PinPosition;
import com.perfectchina.bns.model.treenode.FiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.OpvNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.FiveStarNetTreeNodeRepository;
import com.perfectchina.bns.repositories.OpvNetTreeNodeRepository;
import com.perfectchina.bns.repositories.TreeNodeRepository;

@Service
public class FiveStarTreeNodeServiceImpl extends TreeNodeServiceImpl implements FiveStarTreeNodeService {

	private static final Logger logger = LoggerFactory.getLogger(FiveStarTreeNodeServiceImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);
	
	@Autowired
	private FiveStarNetTreeNodeRepository  fiveStarNetTreeNodeRepository;
	
	@Autowired
	private OpvNetTreeNodeRepository opvNetTreeNodeRepository;

	@Override
	public boolean isReadyToUpdate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TreeNodeRepository getTreeNodeRepository() {
		return fiveStarNetTreeNodeRepository;
	}

	@Override
	protected void process(TreeNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createFiveStarNetTree() {
		OpvNetTreeNode rootTreeNodeOfOPV = opvNetTreeNodeRepository.getRootTreeNodeOfMonth(DateUtils.getCurrentSnapshotDate());
		Stack<OpvNetTreeNode> stk = new Stack<OpvNetTreeNode>();
		stk.push(rootTreeNodeOfOPV);
		while (!stk.empty()) {
			OpvNetTreeNode top = stk.pop();
			for (TreeNode child : top.getChildNodes()) {
				stk.push((OpvNetTreeNode) child);
			}
			FiveStarNetTreeNode fiveStarNetTreeNode = new FiveStarNetTreeNode();
			fiveStarNetTreeNode.setHasChild(top.getHasChild());
			fiveStarNetTreeNode.setSnapshotDate(top.getSnapshotDate());
			fiveStarNetTreeNode.setData(top.getData());
			fiveStarNetTreeNode.setAopv(top.getAopv());
			fiveStarNetTreeNode.setOpv(top.getOpv());
			fiveStarNetTreeNode.setPpv(top.getPpv());
			fiveStarNetTreeNode.setIsActiveMember(top.getPpv()>=200);
			fiveStarNetTreeNode.setLevelNum(top.getLevelNum());
			fiveStarNetTreeNode.setGpv(top.getPpv());
			
			long uplinkId = top.getUplinkId();
			if(uplinkId!=0){
				OpvNetTreeNode one = opvNetTreeNodeRepository.getOne(uplinkId);
				String accountNum = one.getData().getAccountNum();
				FiveStarNetTreeNode one2 = fiveStarNetTreeNodeRepository.getAccountByAccountNum(top.getSnapshotDate(),
						accountNum);
				fiveStarNetTreeNode.setUplinkId(one2.getId());
			}
			
			FiveStarNetTreeNode prevMonthNode = fiveStarNetTreeNodeRepository.getAccountByAccountNum(
					DateUtils.getLastMonthSnapshotDate(top.getSnapshotDate()),
					top.getData().getAccountNum());
			if(prevMonthNode==null){
				fiveStarNetTreeNode.setPin(PinPosition.MEMBER);
			}else{
				fiveStarNetTreeNode.setPin(prevMonthNode.getPin());
			}
			if(fiveStarNetTreeNode.getOpv()>=18000&&fiveStarNetTreeNode.getAopv()>=36000){
				fiveStarNetTreeNode.setPin(PinPosition.NEW_FIVE_STAR);
			}
			
			fiveStarNetTreeNodeRepository.saveAndFlush(fiveStarNetTreeNode);
		}
	}

	@Override
	@Transactional
	public void updateWholeTreeFiveStar() {
		//caculte gpv
		List<FiveStarNetTreeNode> leafNodes = fiveStarNetTreeNodeRepository.findLeafNodes(DateUtils.getCurrentSnapshotDate());
		for (FiveStarNetTreeNode fiveStarNetTreeNode : leafNodes) {
			FiveStarNetTreeNode uplinkNode = fiveStarNetTreeNodeRepository.findOne(fiveStarNetTreeNode.getUplinkId());
			while(uplinkNode!=null){
				if(fiveStarNetTreeNode.getPin()==PinPosition.MEMBER){
					uplinkNode.setGpv(uplinkNode.getGpv()+fiveStarNetTreeNode.getGpv());
					fiveStarNetTreeNode.setGpv(0f);
					fiveStarNetTreeNode.setLevelNum(-1);
				}else{
					FiveStarNetTreeNode uplinkNode2 = uplinkNode;
					while(uplinkNode2.getPin()==PinPosition.MEMBER){
						uplinkNode2 = fiveStarNetTreeNodeRepository.findOne(uplinkNode2.getUplinkId());
						if(uplinkNode2==null){break;}
					}
					fiveStarNetTreeNode.setUplinkId(uplinkNode.getId());
				}
				fiveStarNetTreeNodeRepository.saveAndFlush(fiveStarNetTreeNode);
				fiveStarNetTreeNode = uplinkNode;
				uplinkNode = fiveStarNetTreeNodeRepository.findOne(uplinkNode.getUplinkId());
			}
		}
		//delete no 5star
		fiveStarNetTreeNodeRepository.deleteLevel();
		updataLevel();
	}

	private void updataLevel() {
		int fromLevelNum = 0;
		FiveStarNetTreeNode rootTreeNode = fiveStarNetTreeNodeRepository.getRootTreeNodeOfMonth(DateUtils.getCurrentSnapshotDate());
		
	    Stack<TreeNode> stk = new Stack<TreeNode>();
	    rootTreeNode.setLevelNum(fromLevelNum);	    
	    stk.push(rootTreeNode);

	    while (!stk.empty()) {
	        TreeNode top = stk.pop();
	        int childLevelNum = top.getLevelNum() + 1;
	        for ( TreeNode child : fiveStarNetTreeNodeRepository.getChildNodesByUpid(top.getId())) {
	        	child.setLevelNum( childLevelNum );
	            stk.push(child);
	        }	        
	    }
	}
	
	



}
