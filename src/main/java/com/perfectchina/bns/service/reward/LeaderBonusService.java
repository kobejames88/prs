package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.model.treenode.TreeNode;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/14
 * @Desc:
 */

public interface LeaderBonusService {
    void createRewardNet();

    TreeNode getRootNode(String lastMonthSnapshotDate);
}
