package com.perfectchina.bns.service;

import java.util.Date;

import com.perfectchina.bns.model.treenode.TreeNode;

public interface ActiveNodeService extends TreeNodeService{
	void setPreviousDateEndTime(Date previousDateEndTime);
	void createActiveNetTree(String snapShotDate);
	TreeNode getRootNode(String snapShotDate);
}
