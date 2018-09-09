package com.perfectchina.bns.controller;

import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.service.ActiveNodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "活跃网络图")
public class RestApiActiveNetTreeNodeController {
	public static final Logger logger = LoggerFactory.getLogger(RestApiActiveNetTreeNodeController.class);

	@Autowired
	ActiveNodeService activeNodeService; //Service which will do all data retrieval/manipulation work
	

	// -------------------Retrieve All InterfaceAccountInfos---------------------------------------------
	//@RequestMapping(value = "/activeNet/listAccounts", method = RequestMethod.GET)
	//public ResponseEntity<List<TreeNode>> listAccounts() {
	@RequestMapping(value = "/activeNet/listAccounts/{snapshotDate}", method = RequestMethod.GET)
	@ApiOperation(value = "获取活跃网络图信息")
	public ResponseEntity<List<TreeNode>> listAccounts(@PathVariable("snapshotDate") String snapshotDate) {
		// Retrieve tree from node ; find activeTreeRootNode by last month snapshotDate
		logger.info("Fetching User with snapshotDate {}", snapshotDate);
		TreeNode rootNode = activeNodeService.getRootNode( snapshotDate );
		if ( rootNode == null ) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		// tranverse from root to child
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

	/**
	 * Create a ActiveNetTree base on simpleNetTree
	 * @return
	 */
	@RequestMapping(value = "/activeNet/{snapshotDate}", method = RequestMethod.PUT)
	public ResponseEntity<?> createActiveNet(@PathVariable("snapshotDate") String snapshotDate) {
		//String lastMonthSnapShotDate = DateUtils.getLastMonthSnapshotDate();
		String lastMonthSnapShotDate = snapshotDate;
		activeNodeService.createActiveNetTree(lastMonthSnapShotDate);
		HttpHeaders headers = new HttpHeaders();
		try {
			headers.setLocation( new URI( "/api/ActiveNet/listAccounts" ) );
		} catch (URISyntaxException e) {
			logger.error( e.toString(), e);
		}
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

}