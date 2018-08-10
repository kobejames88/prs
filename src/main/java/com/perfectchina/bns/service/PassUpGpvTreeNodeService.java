package com.perfectchina.bns.service;

import com.perfectchina.bns.model.treenode.TreeNode;

import java.util.Date;

public interface PassUpGpvTreeNodeService extends TreeNodeService{
	void setPreviousDateEndTime(Date previousDateEndTime);
	void updateWholeTreePassUpGPV(String snapShotDate);
	TreeNode getRootNode(String snapShotDate);
}
