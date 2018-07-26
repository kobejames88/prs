package com.perfectchina.bns.common.utils;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.service.TreeNodeServiceImpl;

public class PrintTreeUtil {
	
    private static final Logger logger = LoggerFactory.getLogger(PrintTreeUtil.class);
	
	// DFS
	public static void traverseDFS(TreeNode node) {
	    if (node== null )
	        return;

	    Stack<TreeNode> stk = new Stack<TreeNode>();
	    stk.push(node);

	    while (!stk.empty()) {
	        TreeNode top = stk.pop();
	        for ( TreeNode child : top.getChildNodes()) {
	            stk.push(child);
	        }
	       process(top);
	    }
	}
	
	// BFS
	public static void traverseBFS(TreeNode node) {
	    if (node==null)
	        return;

	    Queue<TreeNode> que = new ArrayDeque<TreeNode>();
	    que.add(node);

	    while (!que.isEmpty()) {
	    	TreeNode front = que.poll();
	        for ( TreeNode child : front.getChildNodes()) {
	            que.add(child);
	        }
	        process(front);
	    }
	}	

	// DFS, bottom
	public static void printBottomUp(TreeNode node) {
	    if (node== null )
	        return;

	    // Traverse the tree
	    Stack<TreeNode> bottomUpStk = new Stack<TreeNode>();
	    
	    Stack<TreeNode> stk = new Stack<TreeNode>();
	    stk.push(node);
	    
	    while (!stk.empty()) {
	        TreeNode top = stk.pop();
	        for ( TreeNode child : top.getChildNodes()) {
	            stk.push(child);
	            bottomUpStk.push(child);
	        }	       
	    	process(top);
	    }
	    
	    
	}
	
	
	private static void process( TreeNode node ) {
		int nodeLevel = node.getLevelNum();
		StringBuffer buf = new StringBuffer();
		for ( int i=0; i< nodeLevel ; i++ ) {
			buf.append("  ");
		}
		buf.append( node.getId() + ", " + node.getData().getAccountNum() );
		logger.debug( buf.toString() );
	}
}
