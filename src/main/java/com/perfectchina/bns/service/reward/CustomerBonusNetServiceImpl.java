package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.CustomerBonusNet;
import com.perfectchina.bns.model.CustomerBonusRate;
import com.perfectchina.bns.model.treenode.OpvNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.CustomerBonusNetRepository;
import com.perfectchina.bns.repositories.CustomerBonusRateRepository;
import com.perfectchina.bns.repositories.OpvNetTreeNodeRepository;
import com.perfectchina.bns.service.TreeNodeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Stack;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/1
 * @Desc:
 */
@Service
public class CustomerBonusNetServiceImpl  implements  CustomerBonusNetService{

    private static final Logger logger = LoggerFactory.getLogger(TreeNodeServiceImpl.class);

    @Autowired
    CustomerBonusNetRepository customerBonusNetRepository;

    @Autowired
    OpvNetTreeNodeRepository opvNetTreeNodeRepository;

    @Autowired
    CustomerBonusRateRepository customerBonusRateRepository;

    public CustomerBonusNetRepository getCustomerBonusNetRepository() {
        return customerBonusNetRepository;
    }

    /**
     * create rewardNetTree base opvNetTree
     */
    @Override
    public void createRewardNet() {
        OpvNetTreeNode fromNode = opvNetTreeNodeRepository.getRootTreeNodeOfMonth(DateUtils.getLastMonthSnapshotDate());

        Stack<TreeNode> stk = new Stack<TreeNode>();
        stk.push(fromNode);

        while (!stk.empty()) {
            TreeNode top = stk.pop();
            CustomerBonusNet customerBonusNet = new CustomerBonusNet();
            for ( TreeNode child : top.getChildNodes()) {
                stk.push(child);
            }
            BeanUtils.copyProperties(top,customerBonusNet);
            //find and set uplinkId
            long uplinkId = top.getUplinkId();
            if(uplinkId!=0){
                OpvNetTreeNode one = opvNetTreeNodeRepository.findById(uplinkId).get();
                String accountNum = one.getData().getAccountNum();
                CustomerBonusNet one2 = customerBonusNetRepository.getAccountByAccountNum(top.getSnapshotDate(),
                        accountNum);
                customerBonusNet.setUplinkId(one2.getId());
            }
            customerBonusNetRepository.saveAndFlush(customerBonusNet);
        }
    }

    /**
     * calculate customer gradation reward
     */
    @Override
    public void calculateReward(String snapShotDate) {
        int maxLevelNum = customerBonusNetRepository.getMaxLevelNum(snapShotDate);
        while(maxLevelNum>0){
            List<CustomerBonusNet> treeNodes = customerBonusNetRepository.getTreeNodesByLevel(maxLevelNum);
            for (CustomerBonusNet customerBonusNet : treeNodes){
                //temporary; opv = this.opv - had calculate opv ;start =  LMAOPV or section start ;
                float opv = customerBonusNet.getOpv();
                float start = customerBonusNet.getAopvLastMonth();
                // amountTotal = section calculate total; reward = amountTotal - child.amountTotal
                float amountTotal = 0 ;
                // base on LMAOPV find rate >= lastMonth.rate asc
                List<CustomerBonusRate> bonusRates = customerBonusRateRepository.findBonusRateByAopvAndDateAsc(customerBonusNet.getAopvLastMonth(), new Date());
                for (CustomerBonusRate customerBonusRate : bonusRates){
                    //the opv need calculate in current section ;this is the last section
                    if(opv+start<=customerBonusRate.getAopv()){
                        amountTotal += opv * customerBonusRate.getBonusRate();
                        float reward =  amountTotal;
                       // List<TreeNode> childNodes = customerBonusNet.getChildNodes();
                        List<CustomerBonusNet> childNodes = customerBonusNetRepository.getChildNodesByUpid(customerBonusNet.getId());
                        //reduce all child.amountTotal
                        for(TreeNode treeNode : childNodes){
                            reward = reward-((CustomerBonusNet)treeNode).getAmountTotal();
                        }
                        customerBonusNet.setReward(reward);
                        customerBonusNet.setAmountTotal(amountTotal);
                        //all section calculate finish
                        break;
                    }
                    else{ //opv + start >current rate.opv
                        //calculate current section reward
                        amountTotal += (customerBonusRate.getAopv() - start) * customerBonusRate.getBonusRate();
                        //modify need calculate opv
                        opv = opv +start - customerBonusRate.getAopv();
                        //modify section start
                        start = customerBonusRate.getAopv();
                    }
                }
                customerBonusNetRepository.saveAndFlush(customerBonusNet);
            }
            maxLevelNum--;
        }
    }

    @Override
    public TreeNode getRootNode(String snapShotDate) {
        TreeNode rootNode = customerBonusNetRepository.getRootTreeNodeOfMonth( snapShotDate );
        return rootNode;
    }


}
