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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.service.AccountService;
import com.perfectchina.bns.service.SimpleTreeNodeService;
import com.perfectchina.bns.service.SimpleTreeNodeServiceImpl;


/**
 * This controller receive request and create simple uplink network with PPV information.
 * @author Steve
 *
 */
@RestController
@RequestMapping("/api")
public class RestApiSimpleNetTreeNodeController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiSimpleNetTreeNodeController.class);


	@Autowired
	SimpleTreeNodeService simpleTreeNodeService; //Service which will do all data retrieval/manipulation work
	
    
	// -------------------Retrieve All InterfaceAccountInfos---------------------------------------------

	@RequestMapping(value = "/simpleNet/listAccounts/{snapshotDate}", method = RequestMethod.GET)
	public ResponseEntity<List<TreeNode>> listAccounts(@PathVariable("snapshotDate") String snapshotDate) {
		
		// pass root node id to retrieve whole tree
		//TreeNode rootNode = simpleTreeNodeService.getTreeNode(1L); 
		TreeNode rootNode = simpleTreeNodeService.getRootTreeNode(snapshotDate);
		
		if ( rootNode == null ) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
			// You many decide to return HttpStatus.NOT_FOUND
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


	/**
	 * Create a InterfaceAccountInfo
	 * This method update the PPV of the simpleNetTreeNode based on the SalesRecord
	 * @return
	 */
	@RequestMapping(value = "/simpleNet/{snapshotDate}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateSimpleNetPpv(@PathVariable("snapshotDate") String snapshotDate) {

		simpleTreeNodeService.updateWholeTree(snapshotDate);
		logger.info("execute, finished updateSimpleNetPpv.");
		 
		HttpHeaders headers = new HttpHeaders();
		try {
			headers.setLocation( new URI( "/api/simpleNet/listAccounts" ) );
		} catch (URISyntaxException e) {
			logger.error( e.toString(), e);
		}

		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

}