package com.perfectchina.bns.controller;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.service.DoubleGoldDiamondTreeNodeService;
import com.perfectchina.bns.service.GoldDiamondTreeNodeService;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;


/**
 * This controller receive request and create network with GPV information.
 * @author Terry
 *
 */
@RestController
@RequestMapping("/api")
public class RestApiDoubleGoldDiamondNetTreeNodeController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiDoubleGoldDiamondNetTreeNodeController.class);

	@Autowired
	DoubleGoldDiamondTreeNodeService treeNodeService; //Service which will do all data retrieval/manipulation work
	
    
	// -------------------Retrieve All InterfaceAccountInfos---------------------------------------------

	@RequestMapping(value = "/doubleGoldDiamond/listAccounts/{snapshotDate}", method = RequestMethod.GET)
	public ResponseEntity<List<TreeNode>> listAccounts(@PathVariable("snapshotDate") String snapshotDate) {
		
		TreeNode rootNode = treeNodeService.getRootTreeNode(snapshotDate); // pass root node id to retrieve whole tree
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
	 * Update pass-up-gpv based on five-star-net-tree
	 * @return
	 */
	@RequestMapping(value = "/doubleGoldDiamond/{snapshotDate}", method = RequestMethod.PUT)
	public ResponseEntity<?> updatePassUpGpvNet(@PathVariable("snapshotDate") String snapshotDate) {
        DateFormat format1 = new SimpleDateFormat("yyyyMM");
        Date date = null;
        try {
            date = format1.parse(snapshotDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        treeNodeService.setPreviousDateEndTime(DateUtils.getCurrentDateEndTime( date ));
        boolean readyToUpdate = treeNodeService.isReadyToUpdate();
        if (readyToUpdate){
            treeNodeService.updateWholeTree(snapshotDate);
            treeNodeService.updateWholeTreeDoubleGoldDiamond(snapshotDate);
        }

		HttpHeaders headers = new HttpHeaders();
		try {
			headers.setLocation( new URI( "/api/doubleGoldDiamond/listAccounts/" + snapshotDate ) );
		} catch (URISyntaxException e) {
			logger.error( e.toString(), e);
		}

		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/doubleGoldDiamond/test/{snapshotDate}", method = RequestMethod.GET)
	public ResponseEntity<?> test(@PathVariable("snapshotDate") String snapshotDate) {

//		treeNodeService.setPreviousDateEndTime(DateUtils.getLastMonthEndDate( new Date() ));
		treeNodeService.updateWholeTree(snapshotDate);
		treeNodeService.updateWholeTreeDoubleGoldDiamond(snapshotDate);

		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

}