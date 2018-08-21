package com.perfectchina.bns.controller.reward;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.CustomerBonusNet;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.service.reward.LeaderBonusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/14
 * @Desc: 领导奖
 */
@RestController
@RequestMapping("/api/reward")
public class RestApiLeaderBonusController {

    public static final Logger logger = LoggerFactory.getLogger(RestApiLeaderBonusController.class);

    @Autowired
    private LeaderBonusService leaderBonusService;

    /**
     * create and calculate leader bonus net tree
     * @return
     */
    @GetMapping(value = "/create/leader/bonus/{snapshotDate}")
    public ResponseEntity<?> createRewardNet(@PathVariable("snapshotDate") String snapshotDate) {

        leaderBonusService.createRewardNet(snapshotDate);

        return new ResponseEntity<String>(HttpStatus.OK);
    }


    /**
     * list all leader bonus net tree information
     * @return
     */
    @GetMapping(value = "/list/leader/bonus/{snapshotDate}")
    public ResponseEntity<List<TreeNode>> listAccounts(@PathVariable("snapshotDate") String snapshotDate) {
        TreeNode rootNode = leaderBonusService.getRootNode(snapshotDate); // pass root node id to retrieve whole tree
        if ( rootNode == null ) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        // parse the whole tree in depth first order for the whole list of TreeNode
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();

        Stack<TreeNode> stk = new Stack<TreeNode>();
        stk.push( rootNode );
        while (!stk.empty()) {
            TreeNode top = stk.pop();
            for ( TreeNode child : top.getChildNodes()) {
                stk.push(child);
            }
            treeNodes.add( top );
        }

        return new ResponseEntity<List<TreeNode>>(treeNodes, HttpStatus.OK);
    }



}
