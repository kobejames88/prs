package com.perfectchina.bns.service;

import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.OpvNetTreeNodeRepository;

import java.util.Date;

public interface OpvTreeNodeService extends TreeNodeService{
	void setPreviousDateEndTime(Date previousDateEndTime);
	void updateWholeTreeOPV(String snapshotDate);
	TreeNode getRootNode(String snapShotDate);
}
