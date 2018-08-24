package com.perfectchina.bns.service;

import com.perfectchina.bns.model.treenode.FiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.model.vo.FiveStarVo;

import java.util.List;

public interface FiveStarTreeNodeService extends TreeNodeService{
	List<FiveStarNetTreeNode> findChildLeafList(String snapShotDate);
	List<FiveStarNetTreeNode> findNodeAtLevel(String snapShotDate, int treeLevelNum);
	void createFiveStarNetTree(String snapshotDate);
	void updateWholeTreeFiveStar(String snapshotDate);
	int getMaxTreeLevel(String snapShotDate);
	TreeNode getRootNode(String snapShotDate);
	List<FiveStarVo> convertFiveStarVo(String snapshotDate);
}
