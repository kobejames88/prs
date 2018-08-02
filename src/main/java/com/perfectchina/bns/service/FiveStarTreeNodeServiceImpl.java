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
import com.perfectchina.bns.model.treenode.OpvNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.FiveStarNetTreeNodeRepository;
import com.perfectchina.bns.repositories.GpvNetTreeNodeRepository;
import com.perfectchina.bns.repositories.OpvNetTreeNodeRepository;
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

	@Override
	public int getMaxTreeLevel() {
		return 0;
	}

    public List<FiveStarNetTreeNode> findChildLeafList(){
        return null;
    }

	public List<FiveStarNetTreeNode> findNodeAtLevel(int treeLevelNum) {
		return null;
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
		GpvNetTreeNode rootTreeNode = gpvNetTreeNodeRepository.getRootTreeNodeOfMonth(DateUtils.getCurrentSnapshotDate());
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

	@Override
	@Transactional
	public void updateWholeTreeFiveStar() {
		//caculte gpv
		List<FiveStarNetTreeNode> leafNodes = fiveStarNetTreeNodeRepository.findLeafNodes(DateUtils.getCurrentSnapshotDate());
		for (FiveStarNetTreeNode fiveStarNetTreeNode : leafNodes) {
			FiveStarNetTreeNode uplinkNode = fiveStarNetTreeNodeRepository.findOne(fiveStarNetTreeNode.getUplinkId());
			while(uplinkNode!=null){
				if(fiveStarNetTreeNode.getPin()==PinPosition.MEMBER){
				}else{
					FiveStarNetTreeNode uplinkNode2 = uplinkNode;
					while(uplinkNode2.getPin()==PinPosition.MEMBER){
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
