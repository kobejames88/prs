package com.perfectchina.bns.service;


import com.perfectchina.bns.model.treenode.FiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.PassUpGpvNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.FiveStarNetTreeNodeRepository;
import com.perfectchina.bns.repositories.PassUpGpvNetTreeNodeRepository;
import com.perfectchina.bns.repositories.TreeNodeRepository;
import com.perfectchina.bns.service.pin.PinPoints;
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
	protected TreeNodeRepository<PassUpGpvNetTreeNode> getTreeNodeRepository() {
		return passUpGpvNetTreeNodeRepository;
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
		TreeNode rootNode = fiveStarNetTreeNodeRepository.getRootTreeNode( snapshotDate );
		updateChildTreeLevel( 0, rootNode,snapshotDate );
	}

	@Override
	public void updateChildTreeLevel(Integer fromLevelNum, TreeNode fromNode,String snapshotDate) {
		super.updateChildTreeLevel(fromLevelNum, fromNode,snapshotDate);
	}

	public int getMaxTreeLevel(String snapShotDate) {
       	Integer maxLevelNum = getTreeNodeRepository().getMaxLevelNum(snapShotDate);
		return maxLevelNum == null ? -1 : maxLevelNum;
	}

	@Override
	protected void process(TreeNode node, String snapshotDate) {
		logger.debug("process, update node=" + node.getData().getAccountNum() + "/" + node.getData().getName()
				+ ", level [" + node.getLevelNum() + "].");
		FiveStarNetTreeNode fiveStarNetTreeNode = (FiveStarNetTreeNode) node;
		PassUpGpvNetTreeNode passUpGpvNetTreeNode = new PassUpGpvNetTreeNode();
		long uplinkId = fiveStarNetTreeNode.getUplinkId();
		if(uplinkId!=0){
			FiveStarNetTreeNode fiveStarUplink = fiveStarNetTreeNodeRepository.getOne(uplinkId);
			String accountNum = fiveStarUplink.getData().getAccountNum();
			PassUpGpvNetTreeNode passUpGpvUplink = getTreeNodeRepository().getAccountByAccountNum(snapshotDate,
					accountNum);
			passUpGpvNetTreeNode.setUplinkId(passUpGpvUplink.getId());
		}

		passUpGpvNetTreeNode.setHasChild(fiveStarNetTreeNode.getHasChild());
		passUpGpvNetTreeNode.setLevelNum(fiveStarNetTreeNode.getLevelNum());
		passUpGpvNetTreeNode.setGpv(fiveStarNetTreeNode.getGpv());
        passUpGpvNetTreeNode.setPpv(fiveStarNetTreeNode.getPpv());
		passUpGpvNetTreeNode.setSnapshotDate(fiveStarNetTreeNode.getSnapshotDate());
		passUpGpvNetTreeNode.setData(fiveStarNetTreeNode.getData());

		passUpGpvNetTreeNodeRepository.saveAndFlush(passUpGpvNetTreeNode);
	}

	private Map<Long,Float> map = new HashMap<>();

	@Override
	/**
	 * Update the entire tree's pass-up-gpv
	 */
	public void updateWholeTreePassUpGPV(String snapShotDate) {
		int treeLevel = getMaxTreeLevel(snapShotDate);
		if (treeLevel < 0)
			return;
		while (treeLevel > 0) {
			// Get all the nodes in this layer
			List<PassUpGpvNetTreeNode> thisTreeLevelList = passUpGpvNetTreeNodeRepository.getTreeNodesByLevelAndSnapshotDate(snapShotDate,treeLevel);
			// loop for the children to calculate OPV at the lowest level
			for (PassUpGpvNetTreeNode passUpGpvNetTreeNode : thisTreeLevelList) {
				long id = passUpGpvNetTreeNode.getId();
				long uplinkId = passUpGpvNetTreeNode.getUplinkId();
				Float gpv = passUpGpvNetTreeNode.getGpv();
                Float tempPoint = map.get(id);
                Float passUpGpv = 0F;
                // Get the qualified line of this node
                int qualifiedLine = passUpGpvNetTreeNode.getQualifiedLine();
				if (gpv != null){
                    if ( tempPoint != null ){
                        passUpGpv = gpv+ tempPoint;
                        Boolean isQualified =  (gpv >= PinPoints.COMMON_QUALIFY_POINTS);
                        if (isAboveRuby(passUpGpv,qualifiedLine)){
                            if ((passUpGpv- PinPoints.COMMON_QUALIFY_POINTS>0) && (isQualified || passUpGpv >= PinPoints.COMMON_QUALIFY_POINTS)){
                                passUpGpvNetTreeNode.setHasAsteriskNode(true);
                                Float asteriskNodePoints = isQualified ? tempPoint : passUpGpv-PinPoints.COMMON_QUALIFY_POINTS;
                                passUpGpvNetTreeNode.setAsteriskNodePoints(asteriskNodePoints);
                                passUpGpv = isQualified ? gpv : PinPoints.COMMON_QUALIFY_POINTS;
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
					// If it is a qualified five-star or ruby,Superior qualification line plus 1
					if (passUpGpv >= PinPoints.COMMON_QUALIFY_POINTS){
						upLinkNode.setQualifiedLine(upLinkNode.getQualifiedLine()+1);
					}else {
						if (isAboveRuby(passUpGpv,qualifiedLine)){
							upLinkNode.setQualifiedLine(upLinkNode.getQualifiedLine()+1);
						}else {
                            upLinkNode.setQualifiedLine(upLinkNode.getQualifiedLine()+qualifiedLine);
                            Float val = map.get(uplinkId);
                            // If there are no empty instructions, there are common superiors,Therefore, we need to add pass-up-gpv
							if(val != null){
								Float value = val;
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
				passUpGpvNetTreeNodeRepository.saveAll(nodes);
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
