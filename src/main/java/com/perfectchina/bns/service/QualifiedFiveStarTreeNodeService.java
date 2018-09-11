package com.perfectchina.bns.service;

import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.model.vo.QualifiedFiveStarVo;

import java.util.Date;
import java.util.List;

public interface QualifiedFiveStarTreeNodeService extends TreeNodeService{
	void setPreviousDateEndTime(Date previousDateEndTime);
	void updateWholeTreeQualifiedFiveStar(String snapShotDate);
    void calculateBottomQualifiedFiveStarReward(String snapShotDate);
    TreeNode getRootNode(String snapShotDate);
	List<QualifiedFiveStarVo> convertQualifiedFiveStarVo(String snapshotDate);
}
