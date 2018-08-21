package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.model.treenode.TreeNode;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/8
 * @Desc:
 */

public interface DistributorBonusNetService {
    void createRewardNet(String lastMonthSnapshotDate);

    TreeNode getRootNode(String lastMonthSnapshotDate);
}
