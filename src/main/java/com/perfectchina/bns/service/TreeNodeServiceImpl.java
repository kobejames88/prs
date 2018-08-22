package com.perfectchina.bns.service;

import java.util.List;
import java.util.Stack;

import com.perfectchina.bns.model.treenode.FiveStarNetTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.SimpleNetTreeNodeRepository;
import com.perfectchina.bns.repositories.TreeNodeRepository;

@Service
public abstract class TreeNodeServiceImpl implements TreeNodeService {
	
    private static final Logger logger = LoggerFactory.getLogger(TreeNodeServiceImpl.class);
    
    @Autowired
	private SimpleNetTreeNodeRepository simpleTreeNodeRepository;

    protected abstract TreeNodeRepository getTreeNodeRepository();
	protected abstract void process( TreeNode node );
    
	public boolean isNodeDataExist(String accountNum) {
		return findByAccountNum( accountNum ) != null && findByAccountNum( accountNum ).size()>0;
	}

	public boolean isNodeDataExist(String accountNum,String snapshotDate) {
		return findByAccountNum( accountNum ,snapshotDate) != null;
	}

	private List<TreeNode> findByAccountNum(String accountNum) {
		return getTreeNodeRepository().findByAccountNum(accountNum);
	}
	private TreeNode findByAccountNum(String accountNum,String snapshotDate) {
		return getTreeNodeRepository().findByAccountNum(snapshotDate,accountNum);
	}
	public TreeNode getTreeNode(Long id) {
		return (TreeNode) getTreeNodeRepository().getOne(id);
	}
	
	public TreeNode getRootTreeNode(String snapshotDate) {
		return (TreeNode) getTreeNodeRepository().getRootTreeNode(snapshotDate);
	}
	
    public void updateWholeTree(String snapshotDate) {
    	// Get child nodes
    	logger.debug("updateWholeTree, start");
    	TreeNode rootNode = getTreeNodeRepository().getRootTreeNode(snapshotDate);
    	updateChildTreeLevel( 0, rootNode );
    	logger.debug("updateWholeTree, end");
    }

    // Return fromNode with all the child updated
	public void updateChildTreeLevel( Integer fromLevelNum, TreeNode fromNode){
		List<TreeNode> childList = fromNode.getChildNodes();
	    if ( ( childList == null ) || ( childList.size() == 0 ) )  {
	        return;
	    }
		logger.debug("updateChildTreeLevel, fromLevelNum="+ fromLevelNum +",  accountNum="+ fromNode.getData().getAccountNum()  );
	    Stack<TreeNode> stk = new Stack<>();
	    fromNode.setLevelNum(fromLevelNum);	    
	    stk.push(fromNode);

	    while (!stk.empty()) {
	        TreeNode top = stk.pop();
	        int childLevelNum = top.getLevelNum() + 1;
	        for ( TreeNode child : top.getChildNodes()) {
	        	child.setLevelNum( childLevelNum );
	            stk.push(child);
	        }
	        process(top);
	    }
	}

}
