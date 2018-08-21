package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.CustomerBonusNetRepository;
import com.perfectchina.bns.service.TreeNodeService;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/1
 * @Desc:
 */

public interface CustomerBonusNetService  {
    void createRewardNet(String snapShotDate);

    void calculateReward(String snapShotDate);
    TreeNode getRootNode(String snapShotDate);
}
