package com.perfectchina.bns.common.utils;

import java.util.ArrayList;

import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.treenode.SimpleNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;

public class PrintTreeUtilTesting {

	public static void main(String args[]) {
		try {
			Account data = new Account();
			data.setAccountNum("A1");
						
			SimpleNetTreeNode nodeA1 = new SimpleNetTreeNode();
			nodeA1.setData(data);
			nodeA1.setLevelNum(0);
			
			ArrayList<TreeNode> childList = new ArrayList<TreeNode>();
						
			data = new Account();
			data.setAccountNum("B1");
			
			SimpleNetTreeNode nodeB1 = new SimpleNetTreeNode();
			nodeB1.setData(data);
			nodeB1.setLevelNum(1);
			
			childList.add(nodeB1);
			
			data = new Account();
			data.setAccountNum("B2");
			
			SimpleNetTreeNode nodeB2 = new SimpleNetTreeNode();
			nodeB2.setData(data);
			nodeB2.setLevelNum(1);
			
			childList.add(nodeB2);
			
			nodeA1.setChildNodes(childList);

			// inside B1, add one more level
			childList = new ArrayList<TreeNode>();
			
			data = new Account();
			data.setAccountNum("C1");
			
			SimpleNetTreeNode node = new SimpleNetTreeNode();
			node.setData(data);
			node.setLevelNum(2);
			
			childList.add(node);
			
			data = new Account();
			data.setAccountNum("C2");
			
			node = new SimpleNetTreeNode();
			node.setData(data);
			node.setLevelNum(2);
			
			childList.add(node);
			
			nodeB1.setChildNodes(childList);
			
			
			// inside B2, add one more level
			childList = new ArrayList<TreeNode>();
			
			data = new Account();
			data.setAccountNum("D1");
			
			node = new SimpleNetTreeNode();
			node.setData(data);
			node.setLevelNum(2);
			
			childList.add(node);
			
			data = new Account();
			data.setAccountNum("D2");
			
			node = new SimpleNetTreeNode();
			node.setData(data);
			node.setLevelNum(2);
			
			childList.add(node);
			
			data = new Account();
			data.setAccountNum("D3");
			
			node = new SimpleNetTreeNode();
			node.setData(data);
			node.setLevelNum(2);
			
			childList.add(node);			
			
			nodeB2.setChildNodes(childList);
			
			
			// show the tree
			PrintTreeUtil.traverseDFS(nodeA1);
			System.out.println("---");
			PrintTreeUtil.printBottomUp(nodeA1);
			
			// PrintTreeUtil.traverseBFS(node1);
			
			
			
			
		} catch (Exception ex ) {
			ex.printStackTrace();
		}
	}
}
