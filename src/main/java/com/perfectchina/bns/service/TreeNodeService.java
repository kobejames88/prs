package com.perfectchina.bns.service;

import com.perfectchina.bns.model.treenode.TreeNode;

public interface TreeNodeService {
		
	boolean isNodeDataExist(String accountNum);
	
	TreeNode getTreeNode(Long id);
	TreeNode getRootTreeNode();
	
	// Child function need to check if the network ready to create or update
	boolean isReadyToUpdate(); 
	
	void updateWholeTree();
	
	void updateChildTreeLevel(Integer fromLevelNum, TreeNode treeNode);
}
