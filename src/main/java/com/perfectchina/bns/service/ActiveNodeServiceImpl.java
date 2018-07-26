package com.perfectchina.bns.service;

import com.perfectchina.bns.model.treenode.ActiveNetTreeNode;
import com.perfectchina.bns.model.treenode.SimpleNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.ActiveNetTreeNodeRepository;
import com.perfectchina.bns.repositories.SimpleNetTreeNodeRepository;
import com.perfectchina.bns.repositories.TreeNodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Service
public class ActiveNodeServiceImpl extends TreeNodeServiceImpl implements ActiveNodeService {

	private static final Logger logger = LoggerFactory.getLogger(ActiveNodeServiceImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);

	@Autowired
	private ActiveNetTreeNodeRepository activeNetTreeNodeRepository;
	@Autowired
	private SimpleNetTreeNodeRepository simpleTreeNodeRepository;

	// Parameter to set calculate PPV for
	// which month
	private Date previousDateEndTime;

	public Date getPreviousDateEndTime() {
		return previousDateEndTime;
	}
	public void setPreviousDateEndTime(Date previousDateEndTime) {
		this.previousDateEndTime = previousDateEndTime;
	}

	@Override
	public boolean isReadyToUpdate() {
		// need to check if Simple Net already exist, otherwise, cannot
		// calculate
		boolean isReady = false;
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

	@Override
	public TreeNodeRepository<ActiveNetTreeNode> getTreeNodeRepository() {
		return activeNetTreeNodeRepository;
	}

	@Override
	protected void process(TreeNode node) {
		// TODO Auto-generated method stub
		logger.debug("process, update node=" + node.getData().getAccountNum() + "/" + node.getData().getName()
				+ ", level [" + node.getLevelNum() + "].");
	}
	@Override
	public void createActiveNetTree() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateWholeTreeActiveNet() {
		// TODO Auto-generated method stub
		
	}

}
