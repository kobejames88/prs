package com.perfectchina.bns.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.service.FiveStarTreeNodeService;


/**
 * This controller receive request and create five star network 
 * @author Steve
 *
 */
@RestController
@RequestMapping("/api")
public class RestApiFiveStarNetTreeNodeController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiFiveStarNetTreeNodeController.class);

	@Autowired
	FiveStarTreeNodeService fiveStarTreeNodeService; //Service which will do all data retrieval/manipulation work
	
    
	// -------------------Retrieve All InterfaceAccountInfos---------------------------------------------

	@RequestMapping(value = "/fiveStarNet/listAccounts", method = RequestMethod.GET)
	public ResponseEntity<List<TreeNode>> listAccounts() {
		//TreeNode rootNode = treeNodeService.getTreeNode(1L); // pass root node id to retrieve whole tree 
		TreeNode rootNode = fiveStarTreeNodeService.getRootTreeNode(); // pass root node id to retrieve whole tree 
		if ( rootNode == null ) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		
		// parse the whole tree in depth first order for the whole list of TreeNode				
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		
	    Stack<TreeNode> stk = new Stack<TreeNode>();
	    stk.push( rootNode );
	    while (!stk.empty()) {
	        TreeNode top = stk.pop();
	        for ( TreeNode child : top.getChildNodes()) {
	            stk.push(child);
	        }
	        treeNodes.add( top );
	    }
	    
	    if ( logger.isDebugEnabled() ) {
	    	for ( TreeNode temp: treeNodes ) {
	    		logger.debug( "listAccounts, treeNodes="+ temp);
	    	}
	    }	    	    
		
		return new ResponseEntity<List<TreeNode>>(treeNodes, HttpStatus.OK);
	}

	
	// -------------------Create a fiveStarNetTree-------------------------------------------

	/**
	 * @return
	 */
	@RequestMapping(value = "/fiveStarNet/", method = RequestMethod.PUT)
	public ResponseEntity<?> createFiveStarNet() {

		Date currentDate = new Date();
		Date previousDateEndTime = DateUtils.getPreviousDateEndTime( currentDate );
		//fiveStarTreeNodeService.setPreviousDateEndTime(previousDateEndTime);
		fiveStarTreeNodeService.createFiveStarNetTree();
		fiveStarTreeNodeService.updateWholeTreeFiveStar();
		
		logger.info("execute, finished Create a fiveStarNetTree.");
		 
		HttpHeaders headers = new HttpHeaders();
		try {
			headers.setLocation( new URI( "/api/fiveStarNet/listAccounts" ) );
		} catch (URISyntaxException e) {
			logger.error( e.toString(), e);
		}

		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

}