package com.perfectchina.bns.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perfectchina.bns.model.treenode.OpvNetTreeNode;
import com.perfectchina.bns.model.treenode.SimpleNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.OpvNetTreeNodeRepository;
import com.perfectchina.bns.repositories.SimpleNetTreeNodeRepository;
import com.perfectchina.bns.repositories.TreeNodeRepository;

@Service
public class FiveStarTreeNodeServiceImpl extends TreeNodeServiceImpl implements FiveStarTreeNodeService {

	private static final Logger logger = LoggerFactory.getLogger(FiveStarTreeNodeServiceImpl.class);

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
