package com.perfectchina.bns.controller;

import com.perfectchina.bns.model.vo.FiveStarVo;
import com.perfectchina.bns.model.vo.GoldDiamonndVo;
import com.perfectchina.bns.model.vo.QualifiedFiveStarVo;
import com.perfectchina.bns.service.FiveStarTreeNodeService;
import com.perfectchina.bns.service.GoldDiamondTreeNodeService;
import com.perfectchina.bns.service.QualifiedFiveStarTreeNodeService;
import com.perfectchina.bns.service.map.MapType;
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
import java.util.List;


/**
 * This controller receive request and create network with PPV, OPV information.
 * @author Terry
 *
 */
@RestController
@RequestMapping("/api")
public class SavePinController {

	public static final Logger logger = LoggerFactory.getLogger(SavePinController.class);

	@Autowired
	QualifiedFiveStarTreeNodeService qualifiedFiveStarTreeNodeService; //Service which will do all data retrieval/manipulation work
	@Autowired
    FiveStarTreeNodeService fiveStarTreeNodeService;
	@Autowired
    GoldDiamondTreeNodeService goldDiamondTreeNodeService;


	@RequestMapping(value = "/pin/{snapshotDate}", method = RequestMethod.PUT)
	public ResponseEntity listAccounts(@PathVariable("snapshotDate") String snapshotDate) {


        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

}