package com.perfectchina.bns.service;

import java.util.Date;

import com.perfectchina.bns.model.treenode.TreeNode;

public interface GpvTreeNodeService extends TreeNodeService{
	void setPreviousDateEndTime(Date previousDateEndTime);
	void updateWholeTreeGPV(String snapshotDate);
	TreeNode getRootNode(String snapShotDate);
}
