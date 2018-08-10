package com.perfectchina.bns.service;


import com.perfectchina.bns.model.treenode.*;
import com.perfectchina.bns.repositories.*;
import com.perfectchina.bns.service.pin.PinPoints;
import com.perfectchina.bns.service.pin.PinPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PassUpGpvTreeNodeServiceImpl extends TreeNodeServiceImpl implements PassUpGpvTreeNodeService {

	private static final Logger logger = LoggerFactory.getLogger(PassUpGpvTreeNodeServiceImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);

	@Autowired
	private FiveStarNetTreeNodeRepository fiveStarNetTreeNodeRepository;
	@Autowired
	private PassUpGpvNetTreeNodeRepository passUpGpvNetTreeNodeRepository;

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
	protected TreeNodeRepository<FiveStarNetTreeNode> getTreeNodeRepository() {
		return fiveStarNetTreeNodeRepository;
	}

	public boolean isReadyToUpdate() {
		// need to check if Simple Net already exist, otherwise, cannot
		// calculate
		boolean isReady = false;
		// // Current month
		String snapshotDate = null;
		try {
			snapshotDate = sdf.format(getPreviousDateEndTime());
			FiveStarNetTreeNode rootNode = fiveStarNetTreeNodeRepository.getRootTreeNodeOfMonth(snapshotDate);

			if (rootNode != null) {
				isReady = true;
			}
		} catch (Exception ex) {
			logger.error("isReadyToUpdate, invalidDate=" + getPreviousDateEndTime());
		}
		return isReady;
	}

	@Override
	public void updateWholeTree(String snapshotDate) {
		TreeNode rootNode = getTreeNodeRepository().getRootTreeNode( snapshotDate );
		updateChildTreeLevel( 0, rootNode );
	}

	@Override
	public void updateChildTreeLevel(Integer fromLevelNum, TreeNode fromNode) {
		super.updateChildTreeLevel(fromLevelNum, fromNode);
	}

	public int getMaxTreeLevel(String snapShotDate) {
       int maxLevelNum = getTreeNodeRepository().getMaxLevelNum(snapShotDate);
        return maxLevelNum;
	}

	/**
	 * param node is SimpleNetTreeNode walking through
	 */
	protected void process(TreeNode node) {
		logger.debug("process, update node=" + node.getData().getAccountNum() + "/" + node.getData().getName()
				+ ", level [" + node.getLevelNum() + "].");
		// Copy the node of the original network map plus the uplinkId to GpvNetTreeNode
		FiveStarNetTreeNode fiveStarNetTreeNode = (FiveStarNetTreeNode) node;
		PassUpGpvNetTreeNode passUpGpvNetTreeNode = new PassUpGpvNetTreeNode();
		//the uplinkId is SimpleNet
		long uplinkId = fiveStarNetTreeNode.getUplinkId();
		if(uplinkId!=0){
			FiveStarNetTreeNode fiveStarUplink = getTreeNodeRepository().getOne(uplinkId);
			String accountNum = fiveStarUplink.getData().getAccountNum();
			PassUpGpvNetTreeNode passUpGpvUplink = passUpGpvNetTreeNodeRepository.getAccountByAccountNum(fiveStarNetTreeNode.getSnapshotDate(),
					accountNum);
			passUpGpvNetTreeNode.setUplinkId(passUpGpvUplink.getId());
		}

		passUpGpvNetTreeNode.setHasChild(fiveStarNetTreeNode.getHasChild());
		passUpGpvNetTreeNode.setLevelNum(fiveStarNetTreeNode.getLevelNum());
		passUpGpvNetTreeNode.setGpv(fiveStarNetTreeNode.getGpv());
		passUpGpvNetTreeNode.setSnapshotDate(fiveStarNetTreeNode.getSnapshotDate());
		passUpGpvNetTreeNode.setData(fiveStarNetTreeNode.getData());

		passUpGpvNetTreeNodeRepository.saveAndFlush(passUpGpvNetTreeNode);
	}

	@Override
	/**
	 * Update the entire tree's pass-up-gpv
	 */
	public void updateWholeTreePassUpGPV(String snapShotDate) {
		// 获取passUpGpvNetTree的最大层级
		int treeLevel = getMaxTreeLevel(snapShotDate);
		if (treeLevel < 0)
			return;
		Map<Long,Float> map = new HashMap<>();
		while (treeLevel >= 0) {
			// 获取这层中的所有节点
			List<PassUpGpvNetTreeNode> thisTreeLevelList = passUpGpvNetTreeNodeRepository.getTreeNodesByLevel(treeLevel);
			// loop for the children to calculate OPV at the lowest level
			for (PassUpGpvNetTreeNode passUpGpvNetTreeNode : thisTreeLevelList) {
				long id = passUpGpvNetTreeNode.getId();
				long uplinkId = passUpGpvNetTreeNode.getUplinkId();
				Float gpv = passUpGpvNetTreeNode.getGpv();
				// 获取紧缩上来的pass-up-gpv
				Float tempPoint = map.get(id);
                Float passUpGpv = 0F;
                // 获取此节点的合格线
                int qualifiedLine = passUpGpvNetTreeNode.getQualifiedLine();
				if (gpv != null){
                    if ( tempPoint != null ){
                        passUpGpv = gpv+ tempPoint;
                        if (isAboveRuby(passUpGpv,qualifiedLine)){
                            if ((passUpGpv- PinPoints.COMMON_QUALIFY_POINTS>0) && (gpv >= PinPoints.COMMON_QUALIFY_POINTS || passUpGpv >= PinPoints.COMMON_QUALIFY_POINTS)){
                                passUpGpvNetTreeNode.setHasAsteriskNode(true);
                                passUpGpvNetTreeNode.setAsteriskNodePoints(passUpGpv-PinPoints.COMMON_QUALIFY_POINTS);
                                passUpGpv = PinPoints.COMMON_QUALIFY_POINTS;
                            }
                        }
                    }else {
                        passUpGpv = gpv;
                    }
				}
                passUpGpvNetTreeNode.setPassUpGpv(passUpGpv);
				List<PassUpGpvNetTreeNode> nodes = new ArrayList<>();
				if(uplinkId != 0){
					PassUpGpvNetTreeNode upLinkNode = passUpGpvNetTreeNodeRepository.getOne(uplinkId);
					// 如果是合格五星或者红宝石，上级合格线加1
					if (passUpGpv >= PinPoints.COMMON_QUALIFY_POINTS){
						upLinkNode.setQualifiedLine(upLinkNode.getQualifiedLine()+1);
					}else {
						if (isAboveRuby(passUpGpv,qualifiedLine)){
							upLinkNode.setQualifiedLine(upLinkNode.getQualifiedLine()+1);
						}else {
							Float mapUplinkId = map.get(uplinkId);
							// 如果不为空说明有共同的上级,因此需要叠加pass-up-gpv
							if(mapUplinkId != null){
								Float value = mapUplinkId;
								Float newVal = value+passUpGpv;
								map.put(uplinkId,newVal);
							}else {
								map.put(uplinkId,passUpGpv);
							}
						}
					}
					nodes.add(upLinkNode);
				}
				nodes.add(passUpGpvNetTreeNode);
				passUpGpvNetTreeNodeRepository.save(nodes);
				passUpGpvNetTreeNodeRepository.flush();
			} // end for loop
			treeLevel--;
		}
	}

	private Boolean isAboveRuby(Float passUpGpv,int qualifiedLine){
		if ((passUpGpv >= PinPoints.RUBY_QUALIFY_POINTS && qualifiedLine > 0) || (qualifiedLine >= 2)){
			return true;
		}
		return false;
	}

	public TreeNode getRootNode(String snapshotDate) {
		TreeNode rootNode = passUpGpvNetTreeNodeRepository.getRootTreeNodeOfMonth( snapshotDate );
		return rootNode;
	}

}
