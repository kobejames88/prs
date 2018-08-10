package com.perfectchina.bns.controller;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.PassUpGpv;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.service.PassUpGpvTreeNodeService;
import com.perfectchina.bns.service.QualifiedFiveStarTreeNodeService;
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
import java.util.*;


/**
 * This controller receive request and create network with GPV information.
 * @author Terry
 *
 */
@RestController
@RequestMapping("/api")
public class RestApiQualifiedFiveStarNetTreeNodeController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiQualifiedFiveStarNetTreeNodeController.class);

	@Autowired
	QualifiedFiveStarTreeNodeService treeNodeService; //Service which will do all data retrieval/manipulation work
	
    
	// -------------------Retrieve All InterfaceAccountInfos---------------------------------------------

	@RequestMapping(value = "/qualifiedFiveStar/listAccounts", method = RequestMethod.GET)
	public ResponseEntity<List<TreeNode>> listAccounts() {
		
		String snapshotDate	= DateUtils.getLastMonthSnapshotDate();	
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
	@RequestMapping(value = "/qualifiedFiveStar/", method = RequestMethod.PUT)
	public ResponseEntity<?> updatePassUpGpvNet() {
		Date currentDate = new Date();
		Date previousDateEndTime = DateUtils.getPreviousDateEndTime( currentDate );
		
		Date lastMonthEndTime = DateUtils.getLastMonthEndDate( currentDate );
		String snapshotDate = DateUtils.getLastMonthSnapshotDate();


		treeNodeService.setPreviousDateEndTime(lastMonthEndTime);
		treeNodeService.updateWholeTree(snapshotDate);
		treeNodeService.updateWholeTreeQualifiedFiveStar(snapshotDate);

		 
		HttpHeaders headers = new HttpHeaders();
		try {
			headers.setLocation( new URI( "/api/qualifiedFiveStar/listAccounts" ) );
		} catch (URISyntaxException e) {
			logger.error( e.toString(), e);
		}

		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

    @RequestMapping(value = "/qualifiedFiveStar/test", method = RequestMethod.GET)
	public void test() {
		Date currentDate = new Date();
		long startTime = System.currentTimeMillis();    //获取开始时间
        // Date previousDateEndTime = DateUtils.getPreviousDateEndTime( currentDate );
		Date lastMonthEndTime = DateUtils.getLastMonthEndDate( currentDate );
		String snapShotDate = DateUtils.getLastMonthSnapshotDate();
        treeNodeService.setPreviousDateEndTime(lastMonthEndTime);
        treeNodeService.updateWholeTree(snapShotDate);
        treeNodeService.updateWholeTreeQualifiedFiveStar(snapShotDate);
        long endTime = System.currentTimeMillis();    //获取结束时间
        logger.info("计算合格五星网络图运行时间： {} ms ",(endTime - startTime));
	}

}