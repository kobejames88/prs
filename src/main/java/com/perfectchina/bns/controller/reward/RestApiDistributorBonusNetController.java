package com.perfectchina.bns.controller.reward;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.service.reward.DistributorBonusNetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/8
 * @Desc:
 */
@RestController
@RequestMapping("/api/reward")
public class RestApiDistributorBonusNetController {

    public static final Logger logger = LoggerFactory.getLogger(RestApiDistributorBonusNetController.class);

    @Autowired
    private DistributorBonusNetService distributorBonusNetService;


    /**
     * create distributorBonusNet
     * @return
     */
    @GetMapping(value = "/create/distributor/bonusnet")
    public ResponseEntity<?> createRewardNet() {

        distributorBonusNetService.createRewardNet();

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    /**
     * list all distributorBonus net tree information
     * @return
     */
    @GetMapping(value = "/list/distributorBonus")
    public ResponseEntity<List<TreeNode>> listAccounts() {
        TreeNode rootNode = distributorBonusNetService.getRootNode(DateUtils.getLastMonthSnapshotDate());
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

        if ( logger.isDebugEnabled() ) {
            for ( TreeNode temp: treeNodes ) {
                logger.debug( "listAccounts, treeNodes="+ temp);
            }
        }

        return new ResponseEntity<List<TreeNode>>(treeNodes, HttpStatus.OK);
    }


}
