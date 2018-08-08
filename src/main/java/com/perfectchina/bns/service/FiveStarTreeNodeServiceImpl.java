package com.perfectchina.bns.service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.treenode.FiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.GpvNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.FiveStarNetTreeNodeRepository;
import com.perfectchina.bns.repositories.GpvNetTreeNodeRepository;
import com.perfectchina.bns.repositories.TreeNodeRepository;
import com.perfectchina.bns.service.pin.PinPosition;

@Service
public class FiveStarTreeNodeServiceImpl extends TreeNodeServiceImpl implements FiveStarTreeNodeService {

	private static final Logger logger = LoggerFactory.getLogger(FiveStarTreeNodeServiceImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);
	
	@Autowired
	private FiveStarNetTreeNodeRepository  fiveStarNetTreeNodeRepository;
	
	@Autowired
	private GpvNetTreeNodeRepository gpvNetTreeNodeRepository;

	@Override
	public boolean isReadyToUpdate() {
		return false;
	}

	public int getMaxTreeLevel(String snapShotDate) {
		return fiveStarNetTreeNodeRepository.getMaxLevelNum(snapShotDate);
	}

    public List<FiveStarNetTreeNode> findChildLeafList(String snapShotDate){
        throw new java.lang.RuntimeException("Not yet implemented");
    }

	public List<FiveStarNetTreeNode> findNodeAtLevel(String snapShotDate, int treeLevelNum) {
        throw new java.lang.RuntimeException("Not yet implemented");
	}

	@Override
	public TreeNodeRepository getTreeNodeRepository() {
		return fiveStarNetTreeNodeRepository;
	}

	@Override
	protected void process(TreeNode node) {
		// TODO Auto-generated method stub
        throw new java.lang.RuntimeException("Not yet implemented");
		
	}

	/**
	 * copy value form gpvNetTree
	 */
	@Override
	public void createFiveStarNetTree() {

		GpvNetTreeNode rootTreeNode = gpvNetTreeNodeRepository.getRootTreeNodeOfMonth(DateUtils.getLastMonthSnapshotDate());
		Stack<GpvNetTreeNode> stk = new Stack<>();

		stk.push(rootTreeNode);
		while (!stk.empty()) {
			GpvNetTreeNode top = stk.pop();
			for (TreeNode child : top.getChildNodes()) {
				stk.push((GpvNetTreeNode) child);
			}
			FiveStarNetTreeNode fiveStarNetTreeNode = new FiveStarNetTreeNode();
			fiveStarNetTreeNode.setHasChild(top.getHasChild());
			fiveStarNetTreeNode.setSnapshotDate(top.getSnapshotDate());
			fiveStarNetTreeNode.setData(top.getData());
			fiveStarNetTreeNode.setAopv(top.getAopv());
			fiveStarNetTreeNode.setOpv(top.getOpv());
			fiveStarNetTreeNode.setAopvLastMonth(top.getAopvLastMonth());
			fiveStarNetTreeNode.setPpv(top.getPpv());
			fiveStarNetTreeNode.setLevelNum(top.getLevelNum());
			fiveStarNetTreeNode.setGpv(top.getGpv());
			fiveStarNetTreeNode.setPin(top.getPin());
			
			long uplinkId = top.getUplinkId();
			if(uplinkId!=0){
				GpvNetTreeNode one = gpvNetTreeNodeRepository.getOne(uplinkId);
				String accountNum = one.getData().getAccountNum();
				FiveStarNetTreeNode one2 = fiveStarNetTreeNodeRepository.getAccountByAccountNum(top.getSnapshotDate(),
						accountNum);
				fiveStarNetTreeNode.setUplinkId(one2.getId());
			}
			
			fiveStarNetTreeNodeRepository.saveAndFlush(fiveStarNetTreeNode);
		}
	}

	/**
	 * after createFiveStarNetTree(copy) then update tree delete some not need information
	 */
	@Override
	@Transactional
	public void updateWholeTreeFiveStar() {
		//caculte gpv
		List<FiveStarNetTreeNode> leafNodes = fiveStarNetTreeNodeRepository.findLeafNodes(DateUtils.getCurrentSnapshotDate());
		for (FiveStarNetTreeNode fiveStarNetTreeNode : leafNodes) {
			FiveStarNetTreeNode uplinkNode = fiveStarNetTreeNodeRepository.findOne(fiveStarNetTreeNode.getUplinkId());
			while(uplinkNode!=null){
				if(PinPosition.MEMBER.equals(fiveStarNetTreeNode.getPin())){
				}else{
					FiveStarNetTreeNode uplinkNode2 = uplinkNode;
					while(PinPosition.MEMBER.equals(uplinkNode2.getPin())){
						uplinkNode2 = fiveStarNetTreeNodeRepository.findOne(uplinkNode2.getUplinkId());
						if(uplinkNode2==null){break;}
					}
					fiveStarNetTreeNode.setUplinkId(uplinkNode2.getId());
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
		FiveStarNetTreeNode rootTreeNode = fiveStarNetTreeNodeRepository.getRootTreeNodeOfMonth(DateUtils.getLastMonthSnapshotDate());
		
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
	
	public TreeNode getRootNode(String snapshotDate) {
		TreeNode rootNode = fiveStarNetTreeNodeRepository.getRootTreeNodeOfMonth( snapshotDate );
		return rootNode;
	}
	
}
