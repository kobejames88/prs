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
import com.perfectchina.bns.service.GpvTreeNodeService;


/**
 * This controller receive request and create network with PPV, OPV information.
 * @author Terry
 *
 */
@RestController
@RequestMapping("/api")
public class RestApiGpvNetTreeNodeController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiGpvNetTreeNodeController.class);

	@Autowired
	GpvTreeNodeService treeNodeService; //Service which will do all data retrieval/manipulation work
	

	// -------------------Retrieve All InterfaceAccountInfos---------------------------------------------

	@RequestMapping(value = "/gpvNet/listAccounts", method = RequestMethod.GET)
	public ResponseEntity<List<TreeNode>> listAccounts() {
		//get gpvNetTree root by last month snapshotDate
		TreeNode rootNode = treeNodeService.getRootNode(DateUtils.getLastMonthSnapshotDate()); // pass root node id to retrieve whole tree
		if ( rootNode == null ) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
			// You many decide to return HttpStatus.NOT_FOUND
		}
		
		// parse the whole tree in depth first order for the whole list of TreeNode				
		List<TreeNode> treeNodes = new ArrayList<>();
		
	    Stack<TreeNode> stk = new Stack<>();
	    stk.push( rootNode );
	    while (!stk.empty()) {
	        TreeNode top = stk.pop();
	        for ( TreeNode child : top.getChildNodes()) {
	            stk.push(child);
	        }
	        treeNodes.add( top );
	    }
		return new ResponseEntity<>(treeNodes, HttpStatus.OK);
	}

	
	// -------------------Create a InterfaceAccountInfo-------------------------------------------

	/**
	 * Update gpv based on SalesRecord
	 * @return
	 */
	@RequestMapping(value = "/gpvNet/", method = RequestMethod.PUT)
	public ResponseEntity<?> updateGpvNet() {
		Date previousDateEndTime = DateUtils.getLastMonthEndDate( new Date() );
		String snapshotDate = DateUtils.convertToSnapShotDate(previousDateEndTime);
		
		treeNodeService.setPreviousDateEndTime(previousDateEndTime);
		treeNodeService.updateWholeTree(snapshotDate);
		treeNodeService.updateWholeTreeGPV(snapshotDate);
		
		logger.info("execute, finished updateSimpleNetPpv.");
		 
		HttpHeaders headers = new HttpHeaders();
		try {
			headers.setLocation( new URI( "/api/gpvNet/listAccounts" ) );
		} catch (URISyntaxException e) {
			logger.error( e.toString(), e);
		}

		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

}