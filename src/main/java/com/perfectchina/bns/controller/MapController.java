package com.perfectchina.bns.controller;

import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.model.vo.QualifiedFiveStarVo;
import com.perfectchina.bns.service.GpvTreeNodeService;
import com.perfectchina.bns.service.QualifiedFiveStarTreeNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 * This controller receive request and create network with PPV, OPV information.
 * @author Terry
 *
 */
@RestController
@RequestMapping("/api")
public class MapController {

	public static final Logger logger = LoggerFactory.getLogger(MapController.class);

	@Autowired
	QualifiedFiveStarTreeNodeService qualifiedFiveStarTreeNodeService; //Service which will do all data retrieval/manipulation work
	

	// -------------------Retrieve All InterfaceAccountInfos---------------------------------------------

	@RequestMapping(value = "/map/{type}/{snapshotDate}", method = RequestMethod.GET)
	public ResponseEntity<List<QualifiedFiveStarVo>> listAccounts(@PathVariable("type") int type, @PathVariable("snapshotDate") String snapshotDate,
                                                                  HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        List<QualifiedFiveStarVo> qualifiedFiveStarVos = null;
	    if (type == 2){
             qualifiedFiveStarVos = qualifiedFiveStarTreeNodeService.convertQualifiedFiveStarVo(snapshotDate);
        }
        return new ResponseEntity<>(qualifiedFiveStarVos, HttpStatus.OK);
	}

}