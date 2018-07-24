package com.perfectchina.bns.controller;

import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.service.ActiveNodeService;
import com.perfectchina.bns.service.FiveStarTreeNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 * This controller receive request and create active network
 * @author Terry
 *
 */
@RestController
@RequestMapping("/api")
public class RestApiActiveNetTreeNodeController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiActiveNetTreeNodeController.class);

	@Autowired
	ActiveNodeService activeNodeService; //Service which will do all data retrieval/manipulation work
	
    
	// -------------------Retrieve All InterfaceAccountInfos---------------------------------------------

	@RequestMapping(value = "/activeNet/listAccounts", method = RequestMethod.GET)
	public ResponseEntity<List<TreeNode>> listAccounts() {
		// 通过根节点获取整棵树
		TreeNode rootNode = activeNodeService.getRootTreeNode();
		if ( rootNode == null ) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		
		// parse the whole tree in depth first order for the whole list of TreeNode
		// 将整个树按深度顺序依次解析整个树列表
		List<TreeNode> treeNodes = new ArrayList<>();
		// 将节点放入堆栈中
	    Stack<TreeNode> stk = new Stack<>();
	    stk.push( rootNode );
	    while (!stk.empty()) {
	        TreeNode top = stk.pop();
	        for ( TreeNode child : top.getChildNodes()) {
	            stk.push(child);
	        }
	        treeNodes.add( top );
	    }
		
		return new ResponseEntity<List<TreeNode>>(treeNodes, HttpStatus.OK);
	}

	
	// -------------------Create a ActiveNetTree-------------------------------------------

	/**
	 * @return
	 */
	@RequestMapping(value = "/ActiveNet/", method = RequestMethod.PUT)
	public ResponseEntity<?> createFiveStarNet() {
		logger.info("execute, finished updateSimpleNetPpv.");
		 
		HttpHeaders headers = new HttpHeaders();
		try {
			headers.setLocation( new URI( "/api/ActiveNet/listAccounts" ) );
		} catch (URISyntaxException e) {
			logger.error( e.toString(), e);
		}
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

}