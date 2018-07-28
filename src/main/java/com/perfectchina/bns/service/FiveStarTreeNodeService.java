package com.perfectchina.bns.service;

import com.perfectchina.bns.model.treenode.FiveStarNetTreeNode;

import java.util.List;

public interface FiveStarTreeNodeService extends TreeNodeService{
	List<FiveStarNetTreeNode> findChildLeafList();
	List<FiveStarNetTreeNode> findNodeAtLevel(int treeLevelNum);
	void createFiveStarNetTree();
	void updateWholeTreeFiveStar();
}
