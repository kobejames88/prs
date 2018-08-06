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
	public QualifiedFiveStarNetTreeNodeRepository getTreeNodeRepository() {
		return qualifiedFiveStarNetTreeNodeRepository;
	}

	public boolean isReadyToUpdate() {
		// need to check if Simple Net already exist, otherwise, cannot
		// calculate
		boolean isReady = false;
		// // Current month
		String snapshotDate = null;
		try {
			snapshotDate = sdf.format(getPreviousDateEndTime());
			QualifiedFiveStarNetTreeNode rootNode = getTreeNodeRepository().getRootTreeNodeOfMonth(snapshotDate);

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

        QualifiedFiveStarNetTreeNode uplink = getTreeNodeRepository().getOne(uplinkId);
        String uplinkAccountNum = uplink.getData().getAccountNum();
        GoldDiamondNetTreeNode uplink2 = goldDiamondNetTreeNodeRepository.getAccountByAccountNum(qualifiedFiveStarNetTreeNode.getSnapshotDate(),
                uplinkAccountNum);
        goldDiamondNetTreeNode.setUplinkId(uplink2.getId());

        // 判断是否为金钻
        if (StringUtils.equals(PinPosition.GOLD_DIAMOND,pin)){
            // 如果是金钻
            // key => id , value => id
            copyNetTree(qualifiedFiveStarNetTreeNode,goldDiamondNetTreeNode);
        }else{
            // 如果不是金钻
            // key => id , value => id
//            if ((passUpGpv >= 9000 && qualifiedLine >= 1) || (qualifiedLine >= 2)){
//                copyNetTree(passUpGpvNetTreeNode,qualifiedFiveStarNetTreeNode);
//            }else {
//                // 过滤此元素
//                // 获取此元素的直接下级
//                List<TreeNode> childNodes = passUpGpvNetTreeNodeRepository.getChildNodesByUpid(passUpGpvNetTreeNode.getId());
//                // 将此元素的合格线给上级
//                QualifiedFiveStarNetTreeNode uplink = qualifiedFiveStarNetTreeNodeRepository.getOne(one2.getId());
//                uplink.setQualifiedLine(uplink.getQualifiedLine()+passUpGpvNetTreeNode.getQualifiedLine());
//                // 将此元素的直接下级并到上级
//                for (TreeNode childNode : childNodes){
//                    PassUpGpvNetTreeNode passUpGpvNetTreeChildNode = (PassUpGpvNetTreeNode)childNode;
//                    passUpGpvNetTreeChildNode.setUplinkId(uplinkId);
//                    passUpGpvNetTreeChildNode.setLevelNum(childNode.getLevelNum()-1);
//                    passUpGpvNetTreeNodeRepository.saveAndFlush(passUpGpvNetTreeChildNode);
//                }
//                qualifiedFiveStarNetTreeNodeRepository.saveAndFlush(one2);
//            }
        }
	}

	private void copyNetTree(QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode,GoldDiamondNetTreeNode goldDiamondNetTreeNode){
        String accountNum = qualifiedFiveStarNetTreeNode.getData().getAccountNum();
        String snapshotDate = sdf.format(getPreviousDateEndTime());
        OpvNetTreeNode opvNetTreeNode = opvNetTreeNodeRepository.findByAccountNum(accountNum,snapshotDate);

        goldDiamondNetTreeNode.setOpv(opvNetTreeNode.getOpv());
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
