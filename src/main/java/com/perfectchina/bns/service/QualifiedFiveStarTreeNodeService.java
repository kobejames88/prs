package com.perfectchina.bns.service;

import com.perfectchina.bns.model.treenode.TreeNode;

import java.util.Date;

public interface QualifiedFiveStarTreeNodeService extends TreeNodeService{
	void setPreviousDateEndTime(Date previousDateEndTime);
	void updateWholeTreeQualifiedFiveStar(String snapShotDate);
    TreeNode getRootNode(String snapShotDate);
}
