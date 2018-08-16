package com.perfectchina.bns.service;

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
public class DoubleGoldDiamondTreeNodeServiceImpl extends TreeNodeServiceImpl implements DoubleGoldDiamondTreeNodeService {

	private static final Logger logger = LoggerFactory.getLogger(DoubleGoldDiamondTreeNodeServiceImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);

	@Autowired
	private GoldDiamondNetTreeNodeRepository goldDiamondNetTreeNodeRepository;

	@Autowired
	private DoubleGoldDiamondNetTreeNodeRepository doubleGoldDiamondNetTreeNodeRepository;

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
	public TreeNodeRepository<DoubleGoldDiamondNetTreeNode> getTreeNodeRepository() {
		return doubleGoldDiamondNetTreeNodeRepository;
	}

	public boolean isReadyToUpdate() {
		// need to check if Simple Net already exist, otherwise, cannot
		// calculate
		boolean isReady = false;
		// // Current month
		String snapshotDate = null;
		try {
			snapshotDate = sdf.format(getPreviousDateEndTime());
			GoldDiamondNetTreeNode rootNode = goldDiamondNetTreeNodeRepository.getRootTreeNodeOfMonth(snapshotDate);

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
		TreeNode rootNode = goldDiamondNetTreeNodeRepository.getRootTreeNode(snapshotDate);
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

	private Map<Long,Long> relation = new HashMap<>();

	/**
	 * param node is SimpleNetTreeNode walking through
	 */
	protected void process(TreeNode node) {
		logger.debug("process, update node=" + node.getData().getAccountNum() + "/" + node.getData().getName()
				+ ", level [" + node.getLevelNum() + "].");
		// 当前元素
		GoldDiamondNetTreeNode goldDiamondNetTreeNode = (GoldDiamondNetTreeNode) node;
		// 待装载元素
		DoubleGoldDiamondNetTreeNode doubleGoldDiamondNetTreeNode = new DoubleGoldDiamondNetTreeNode();
		// 当前元素id
        Long id = goldDiamondNetTreeNode.getId();
        // 获取当前元素的opv
        String accountNum = goldDiamondNetTreeNode.getData().getAccountNum();
        String snapshotDate = sdf.format(getPreviousDateEndTime());
        OpvNetTreeNode opvNetTreeNode = opvNetTreeNodeRepository.findByAccountNum(snapshotDate,accountNum);

        // 获取当前元素的所有直接下级
        List<GoldDiamondNetTreeNode> childNodes = goldDiamondNetTreeNodeRepository.getChildNodesByUpid(id);
        int count = childNodes.size();
        Boolean flag = false;
        if (count >= 7){
            // 如果有 x>=7 个直接下级，尝试合并
            Float mergingPoints = calculateMergingPoints(7, count, childNodes);
            if (mergingPoints >= 1000000F){

            }
            if (mergingPoints >= 500000F){

            }
            if (mergingPoints >= 200000F){

            }
            if (mergingPoints < 200000F){
                flag = true;
            }
        }

        if (flag || (count >= 4 && count < 7)){
            // 如果有 x>=7 个直接下级但是合并失败 或 4<= x <7, 尝试合并

        }

        Long map_uplinkId = relation.get(id);
        GoldDiamondNetTreeNode goldDiamondUplink = goldDiamondNetTreeNodeRepository.getOne(map_uplinkId);

        String uplinkAccountNum = goldDiamondUplink.getData().getAccountNum();
        DoubleGoldDiamondNetTreeNode doubleGoldDiamondUplink = getTreeNodeRepository().getAccountByAccountNum(goldDiamondNetTreeNode.getSnapshotDate(),
                uplinkAccountNum);

	}

	private Float calculateMergingPoints(int line,int count,List<GoldDiamondNetTreeNode> childNodes){
	    int avg = count/line;
	    int mod = count%line;
	    Float[] every = new Float[line];
        Float total = 0F;
	    for (int i=0;i<line;i++){
	        int step = (i==line-1) ? avg+mod : avg;
	        for (int j=i*avg;j<step;j++){
                every[i] += childNodes.get(j).getPassUpOpv();
            }
            total = (i==0) ? every[i] : every[i]+total;
        }
        return total;
    }

    private void setuplinkLevelLineAndLevel(String uplinkLevelLine,GoldDiamondNetTreeNode goldDiamondNetTreeNode,Long goldDiamondUplinkId){
        String newUplinkLevelLine = String.valueOf(new StringBuilder().append(uplinkLevelLine).append("_").append(goldDiamondUplinkId));
        String[] newUplinkLevelLines = StringUtils.split(newUplinkLevelLine, "_");
        int level = newUplinkLevelLines.length-1;
        goldDiamondNetTreeNode.setLevelLine(newUplinkLevelLine);
        goldDiamondNetTreeNode.setLevelNum(level);
    }

	private void copyNetTree(QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode,GoldDiamondNetTreeNode goldDiamondNetTreeNode){
        goldDiamondNetTreeNode.setSnapshotDate(qualifiedFiveStarNetTreeNode.getSnapshotDate());
        goldDiamondNetTreeNode.setData(qualifiedFiveStarNetTreeNode.getData());
		goldDiamondNetTreeNodeRepository.saveAndFlush(goldDiamondNetTreeNode);
	}

	@Override
	/**
	 * Update the entire tree's pass-up-gpv
	 */
	public void updateWholeTreeDoubleGoldDiamond(String snapshotDate) {
		// Get the level of the original tree
		int treeLevel = getMaxTreeLevel(snapshotDate);
		if (treeLevel < 0)
			return;
		Map<Long,List<PassUpGpv>> downLines = new HashMap<>();
		while (treeLevel >= 0) {
			List<GoldDiamondNetTreeNode> thisTreeLevelTreeList = goldDiamondNetTreeNodeRepository.getTreeNodesByLevel(treeLevel);
			// loop for the children to calculate OPV at the lowest level
			for (GoldDiamondNetTreeNode goldDiamondNetTreeNode : thisTreeLevelTreeList) {
                long id = goldDiamondNetTreeNode.getId();
                List<GoldDiamondNetTreeNode> childs = goldDiamondNetTreeNodeRepository.getChildNodesByUpid(id);
                if (childs.size()>0){
                    goldDiamondNetTreeNode.setHasChild(true);
                }else {
                    goldDiamondNetTreeNode.setHasChild(false);
                }
                goldDiamondNetTreeNodeRepository.saveAndFlush(goldDiamondNetTreeNode);
			} // end for loop
			treeLevel--;
		}
	}

	public TreeNode getRootNode(String snapshotDate) {
		TreeNode rootNode = goldDiamondNetTreeNodeRepository.getRootTreeNodeOfMonth( snapshotDate );
		return rootNode;
	}

}
