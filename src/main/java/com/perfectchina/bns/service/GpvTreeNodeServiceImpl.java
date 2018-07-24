package com.perfectchina.bns.service;

import com.perfectchina.bns.common.utils.Const;
import com.perfectchina.bns.model.treenode.GpvNetTreeNode;
import com.perfectchina.bns.model.treenode.OpvNetTreeNode;
import com.perfectchina.bns.model.treenode.SimpleNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.GpvNetTreeNodeRepository;
import com.perfectchina.bns.repositories.OpvNetTreeNodeRepository;
import com.perfectchina.bns.repositories.SimpleNetTreeNodeRepository;
import com.perfectchina.bns.repositories.TreeNodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GpvTreeNodeServiceImpl extends TreeNodeServiceImpl implements GpvTreeNodeService {

	private static final Logger logger = LoggerFactory.getLogger(GpvTreeNodeServiceImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);

//	@Autowired
//	private TreeNodeRepository<SimpleNetTreeNode> simpleTreeNodeRepository;
//	@Autowired
//	private TreeNodeRepository<OpvNetTreeNode> opvTreeNodeRepository;
	@Autowired
	private SimpleNetTreeNodeRepository simpleTreeNodeRepository;
	@Autowired
	private GpvNetTreeNodeRepository gpvNetTreeNodeRepository;

	
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
	public TreeNodeRepository<GpvNetTreeNode> getTreeNodeRepository() {
		return gpvNetTreeNodeRepository;
	}

	public boolean isReadyToUpdate() {
		// need to check if Simple Net already exist, otherwise, cannot
		// calculate
		boolean isReady = false;
		// 当前月份
		String snapshotDate = null;
		try {
			snapshotDate = sdf.format(getPreviousDateEndTime());
			SimpleNetTreeNode rootNode = simpleTreeNodeRepository.getRootTreeNodeOfMonth(snapshotDate);
			if (rootNode != null) {
				isReady = true;
			}
		} catch (Exception ex) {
			logger.error("isReadyToUpdate, invalidDate=" + getPreviousDateEndTime());
		}
		return isReady;
	}

	/**
	 * param node is SimpleNetTreeNode walking through
	 */
	protected void process(TreeNode node) {
		logger.debug("process, update node=" + node.getData().getAccountNum() + "/" + node.getData().getName()
				+ ", level [" + node.getLevelNum() + "].");
		// 将原始网络图的节点加上上级id复制到GpvNetTreeNode
		SimpleNetTreeNode simpleNetTreeNode = (SimpleNetTreeNode) node;
		GpvNetTreeNode gpvNetTreeNode = new GpvNetTreeNode();
		//the uplinkId is SimpleNet
		// opvNetTreeNode.setUplinkId( simpleNetTreeNode.getUplinkId() ); 
		long uplinkId = simpleNetTreeNode.getUplinkId();
		if(uplinkId!=0){
			SimpleNetTreeNode one = simpleTreeNodeRepository.getOne(uplinkId);
			String accountNum = one.getData().getAccountNum();
			GpvNetTreeNode one2 = gpvNetTreeNodeRepository.getAccountByAccountNum(simpleNetTreeNode.getSnapshotDate(),
					accountNum);
			gpvNetTreeNode.setUplinkId(one2.getId());
		}

		gpvNetTreeNode.setHasChild(simpleNetTreeNode.getHasChild());
		gpvNetTreeNode.setLevelNum(simpleNetTreeNode.getLevelNum());
		gpvNetTreeNode.setPpv(simpleNetTreeNode.getPpv());
		gpvNetTreeNode.setSnapshotDate(simpleNetTreeNode.getSnapshotDate());
		gpvNetTreeNode.setData(simpleNetTreeNode.getData());

		// find out AOPV from last month of the OPVNetTreeNode for the same account
		Long accountId = node.getData().getId();


		// retrieve child level OPV for the current month, then add current node OPV
		// 检查当前月份的子级opv，并加上当前节点的opv

		// throw new IllegalArgumentException("Not finished yet.");

		gpvNetTreeNodeRepository.saveAndFlush(gpvNetTreeNode);
	}

	@Override
	/**
	 * 更新整棵树的gpv
	 */
	public void updateWholeTreeGPV() {
		// 获取原始树的代数
		int treeLevel = getTreeLevel();
		if (treeLevel < 0)
			return;
		Map<Long,Float> map = new HashMap();
		while (treeLevel >= 0) {
			List<GpvNetTreeNode> thisTreeLevelTreeList = gpvNetTreeNodeRepository.getTreeNodesByLevel(treeLevel);
			// loop for the children to calculate OPV at the lowest level
			for (GpvNetTreeNode opvNetTreeNode : thisTreeLevelTreeList) {
				// 判断该会员是否五星及以上
				if (Const.PinEnum.valOf(opvNetTreeNode.getData().getPin()).getCode() >= 6){
					opvNetTreeNode.setGpv(opvNetTreeNode.getPpv());
				}else {
					// 判断id是否在map的key中
					if (map.containsKey(opvNetTreeNode.getId())){
						opvNetTreeNode.setGpv(opvNetTreeNode.getPpv()+map.get(opvNetTreeNode.getId()));
					}else {
						opvNetTreeNode.setGpv(opvNetTreeNode.getPpv());
					}
					if(map.containsKey(opvNetTreeNode.getUplinkId())){
						Float value = map.get(opvNetTreeNode.getUplinkId());
						Float newVal = value+opvNetTreeNode.getGpv();
						map.put(opvNetTreeNode.getUplinkId(),newVal);
					}else {
						map.put(opvNetTreeNode.getUplinkId(),opvNetTreeNode.getGpv());
					}
				}
				gpvNetTreeNodeRepository.saveAndFlush(opvNetTreeNode);
			} // end for loop
			treeLevel--;
		}
	}

	/**
	 * 获取原始树的代数
	 * @return
	 */
	private int getTreeLevel() {
		// get root node
		TreeNode fromNode = simpleTreeNodeRepository.getRootTreeNode();
		int treeLevel = 0;
		
		Stack<TreeNode> stk = new Stack<>();
		stk.push(fromNode);
		while (!stk.empty()) {
			TreeNode top = stk.pop();
			for (TreeNode child : top.getChildNodes()) {
				treeLevel = treeLevel > child.getLevelNum() ? treeLevel : child.getLevelNum();
				stk.push(child);
			}
		}
		return treeLevel;
	}

}
