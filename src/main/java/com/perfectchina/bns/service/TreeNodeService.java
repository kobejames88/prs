package com.perfectchina.bns.service;

import java.util.List;

import com.perfectchina.bns.model.treenode.FiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;

public interface TreeNodeService<T> {
		
	boolean isNodeDataExist(String accountNum);
	boolean isNodeDataExist(String accountNum,String snapshotDate);

	TreeNode getTreeNode(Long id);
	TreeNode getRootTreeNode(String snapshotDate);
	
	// Child function need to check if the network ready to create or update
	boolean isReadyToUpdate(); 
	
	void updateWholeTree(String snapshotDate);
	
	void updateChildTreeLevel(Integer fromLevelNum, TreeNode treeNode);

	//int getMaxTreeLevel(String snapShotDate);
}
