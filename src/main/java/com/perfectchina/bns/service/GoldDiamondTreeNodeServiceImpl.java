package com.perfectchina.bns.service;

import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.PassUpGpv;
import com.perfectchina.bns.model.treenode.*;
import com.perfectchina.bns.repositories.*;
import com.perfectchina.bns.service.pin.PinPosition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GoldDiamondTreeNodeServiceImpl extends TreeNodeServiceImpl implements GoldDiamondTreeNodeService {

	private static final Logger logger = LoggerFactory.getLogger(GoldDiamondTreeNodeServiceImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);

	@Autowired
	private GoldDiamondNetTreeNodeRepository goldDiamondNetTreeNodeRepository;
	@Autowired
	private QualifiedFiveStarNetTreeNodeRepository qualifiedFiveStarNetTreeNodeRepository;
	@Autowired
    private OpvNetTreeNodeRepository opvNetTreeNodeRepository;

	private Date previousDateEndTime; // Parameter to set calculate PPV for
										// which month

	public Date getPreviousDateEndTime() {
		return previousDateEndTime;
	}

	public void setPreviousDateEndTime(Date previousDateEndTime) {
		this.previousDateEndTime = previousDateEndTime;
	}

	// Need to walk through simple net, therefore, return simple net tree node
	// repository
	public TreeNodeRepository<GoldDiamondNetTreeNode> getTreeNodeRepository() {
		return goldDiamondNetTreeNodeRepository;
	}

	public boolean isReadyToUpdate() {
		// need to check if Simple Net already exist, otherwise, cannot
		// calculate
		boolean isReady = false;
		// // Current month
		String snapshotDate = null;
		try {
			snapshotDate = sdf.format(getPreviousDateEndTime());
			QualifiedFiveStarNetTreeNode rootNode = qualifiedFiveStarNetTreeNodeRepository.getRootTreeNodeOfMonth(snapshotDate);

			if (rootNode != null) {
				isReady = true;
			}
		} catch (Exception ex) {
			logger.error("isReadyToUpdate, invalidDate=" + getPreviousDateEndTime());
		}
		return isReady;
	}


	@Override
	public void updateChildTreeLevel(Integer fromLevelNum, TreeNode fromNode) {
		super.updateChildTreeLevel(fromLevelNum, fromNode);
	}

	private Map<Long,Long> relation = new HashMap<>();

	/**
	 * param node is SimpleNetTreeNode walking through
	 */
	protected void process(TreeNode node) {
		logger.debug("process, update node=" + node.getData().getAccountNum() + "/" + node.getData().getName()
				+ ", level [" + node.getLevelNum() + "].");
		// Copy the node of the original network map plus the uplinkId to GpvNetTreeNode
		// 当前元素
		QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode = (QualifiedFiveStarNetTreeNode) node;
		// 待装载元素
		GoldDiamondNetTreeNode goldDiamondNetTreeNode = new GoldDiamondNetTreeNode();
		//the uplinkId is SimpleNet
		long uplinkId = qualifiedFiveStarNetTreeNode.getUplinkId();
        String pin = qualifiedFiveStarNetTreeNode.getData().getPin();
        Long id = qualifiedFiveStarNetTreeNode.getId();
        Long map_uplinkId = relation.get(id);

        String accountNum = qualifiedFiveStarNetTreeNode.getData().getAccountNum();
        String snapshotDate = sdf.format(getPreviousDateEndTime());
        OpvNetTreeNode opvNetTreeNode = opvNetTreeNodeRepository.findByAccountNum(accountNum,snapshotDate);

        // 判断是否为金钻
        if (StringUtils.equals(PinPosition.GOLD_DIAMOND,pin)){
            // 如果是金钻
            // key => 下级id , value => id
            if (map_uplinkId != null){
                goldDiamondNetTreeNode.setUplinkId(map_uplinkId);
            }else {
                goldDiamondNetTreeNode.setUplinkId(0);
            }
            goldDiamondNetTreeNode.setPassUpOpv(opvNetTreeNode.getOpv());
            // 获取金钻的所有直接下级
            List<TreeNode> childNodes = qualifiedFiveStarNetTreeNode.getChildNodes();
            for (TreeNode childNode : childNodes){
                QualifiedFiveStarNetTreeNode qualifiedFiveChildNode = (QualifiedFiveStarNetTreeNode)childNode;
                relation.put(qualifiedFiveChildNode.getId(),qualifiedFiveStarNetTreeNode.getId());
            }
            copyNetTree(qualifiedFiveStarNetTreeNode,goldDiamondNetTreeNode);
        }else{
            // 如果不是金钻
            // 判断是否根节点
            // key => 下级id , value => G_id
            if(uplinkId != 0){
                if (map_uplinkId != null){
                    // 有金钻上级
                    QualifiedFiveStarNetTreeNode qualifiedFiveStarUplink = qualifiedFiveStarNetTreeNodeRepository.getOne(map_uplinkId);
                    String uplinkAccountNum = qualifiedFiveStarUplink.getData().getAccountNum();
                    GoldDiamondNetTreeNode goldDiamondUplink = getTreeNodeRepository().getAccountByAccountNum(qualifiedFiveStarNetTreeNode.getSnapshotDate(),
                            uplinkAccountNum);
                    goldDiamondUplink.setPassUpOpv(goldDiamondUplink.getOpv()+opvNetTreeNode.getOpv());
                    // 获取当前元素的所有直接下级
                    List<TreeNode> childNodes = qualifiedFiveStarNetTreeNode.getChildNodes();
                    for (TreeNode childNode : childNodes){
                        QualifiedFiveStarNetTreeNode qualifiedFiveChildNode = (QualifiedFiveStarNetTreeNode)childNode;
                        relation.put(qualifiedFiveChildNode.getId(),map_uplinkId);
                    }
                    goldDiamondNetTreeNodeRepository.saveAndFlush(goldDiamondUplink);
                    relation.remove(id);
                }
            }
        }
	}

	private void copyNetTree(QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode,GoldDiamondNetTreeNode goldDiamondNetTreeNode){
        goldDiamondNetTreeNode.setHasChild(qualifiedFiveStarNetTreeNode.getHasChild());
        goldDiamondNetTreeNode.setLevelNum(qualifiedFiveStarNetTreeNode.getLevelNum());
        goldDiamondNetTreeNode.setSnapshotDate(qualifiedFiveStarNetTreeNode.getSnapshotDate());
        goldDiamondNetTreeNode.setData(qualifiedFiveStarNetTreeNode.getData());
		goldDiamondNetTreeNodeRepository.saveAndFlush(goldDiamondNetTreeNode);
	}

	@Override
	/**
	 * Update the entire tree's pass-up-gpv
	 */
	public void updateWholeTreeGoldDiamond(String snapshotDate) {
		// Get the level of the original tree
		int treeLevel = qualifiedFiveStarNetTreeNodeRepository.getMaxLevelNum(snapshotDate);
		if (treeLevel < 0)
			return;
		Map<Long,List<PassUpGpv>> downLines = new HashMap<>();
		while (treeLevel >= 0) {
			List<QualifiedFiveStarNetTreeNode> thisTreeLevelTreeList = qualifiedFiveStarNetTreeNodeRepository.getTreeNodesByLevel(treeLevel);
			// loop for the children to calculate OPV at the lowest level
			for (QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode : thisTreeLevelTreeList) {


			} // end for loop
			treeLevel--;
		}
	}



}
