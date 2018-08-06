package com.perfectchina.bns.controller.reward;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.controller.RestApiOpvNetTreeNodeController;
import com.perfectchina.bns.model.CustomerBonusNet;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.service.reward.CustomerBonusNetService;
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
 * @Date: 2018/8/1
 * @Desc:
 */
@RestController
@RequestMapping("/api/reward")
public class CustomerDifferentialRewardController {

    public static final Logger logger = LoggerFactory.getLogger(RestApiOpvNetTreeNodeController.class);

    @Autowired
    CustomerBonusNetService customerBonusNetService;


    /**
     * create customer gradation reward net tree
     * @return
     */
    @GetMapping(value = "/create/rewardnet")
    public ResponseEntity<?> createRewardNet() {

    	String lastMonthSnapShotDate = DateUtils.getLastMonthSnapshotDate();
        customerBonusNetService.createRewardNet();
        customerBonusNetService.calculateReward(lastMonthSnapShotDate);

        logger.info("execute, finished calculate customer reward.");

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    /**
     * list all reward net tree information
     * @return
     */
    @GetMapping(value = "/list/rewardnet")
    public ResponseEntity<List<CustomerBonusNet>> listAccounts() {
        CustomerBonusNet rootNode = customerBonusNetService.getCustomerBonusNetRepository().getRootTreeNodeOfMonth(DateUtils.getCurrentSnapshotDate()); // pass root node id to retrieve whole tree
        if ( rootNode == null ) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        // parse the whole tree in depth first order for the whole list of TreeNode
        List<CustomerBonusNet> treeNodes = new ArrayList<CustomerBonusNet>();

        Stack<CustomerBonusNet> stk = new Stack<CustomerBonusNet>();
        stk.push( rootNode );
        while (!stk.empty()) {
            CustomerBonusNet top = stk.pop();
            for ( TreeNode child : top.getChildNodes()) {
                stk.push((CustomerBonusNet)child);
            }
            treeNodes.add( top );
        }

        if ( logger.isDebugEnabled() ) {
            for ( TreeNode temp: treeNodes ) {
                logger.debug( "listAccounts, treeNodes="+ temp);
            }
        }

        return new ResponseEntity<List<CustomerBonusNet>>(treeNodes, HttpStatus.OK);
    }


}
