package com.perfectchina.bns.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

	@RequestMapping(value = "/gpvNet/listAccounts/{snapshotDate}", method = RequestMethod.GET)
	public ResponseEntity<List<TreeNode>> listAccounts(@PathVariable("snapshotDate") String snapshotDate) {
		//get gpvNetTree root by last month snapshotDate
		TreeNode rootNode = treeNodeService.getRootNode(snapshotDate); // pass root node id to retrieve whole tree
		List<TreeNode> childNodes = rootNode.getChildNodes();
		if ( rootNode == null || childNodes.size() == 0 ) {
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
	@RequestMapping(value = "/gpvNet/{snapshotDate}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateGpvNet(@PathVariable("snapshotDate") String snapshotDate) {
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
            treeNodeService.updateWholeTreeGPV(snapshotDate);
        }
		
		logger.info("execute, finished updateSimpleNetPpv.");
		 
		HttpHeaders headers = new HttpHeaders();
		try {
			headers.setLocation( new URI( "/api/gpvNet/listAccounts/" + snapshotDate ) );
		} catch (URISyntaxException e) {
			logger.error( e.toString(), e);
		}

		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

}