package com.perfectchina.bns.service;

import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.TreeNodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Locale;

@Service
public class ActiveNodeServiceImpl extends TreeNodeServiceImpl implements ActiveNodeService {

	private static final Logger logger = LoggerFactory.getLogger(ActiveNodeServiceImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);

	@Override
	public boolean isReadyToUpdate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TreeNodeRepository getTreeNodeRepository() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void process(TreeNode node) {
		// TODO Auto-generated method stub
		
	}



}
