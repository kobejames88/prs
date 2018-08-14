package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.model.treenode.TreeNode;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/8
 * @Desc:
 */

public interface DistributorDifferentialBonusNetService {
    void createRewardNet();

    TreeNode getRootNode(String lastMonthSnapshotDate);
}
