package com.perfectchina.bns.controller;

import com.perfectchina.bns.model.vo.*;
import com.perfectchina.bns.service.*;
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
public class MapController {

	public static final Logger logger = LoggerFactory.getLogger(MapController.class);

	@Autowired
    SimpleTreeNodeService simpleTreeNodeService;
	@Autowired
	QualifiedFiveStarTreeNodeService qualifiedFiveStarTreeNodeService; //Service which will do all data retrieval/manipulation work
	@Autowired
    FiveStarTreeNodeService fiveStarTreeNodeService;
	@Autowired
    GoldDiamondTreeNodeService goldDiamondTreeNodeService;
	@Autowired
    DoubleGoldDiamondTreeNodeService doubleGoldDiamondTreeNodeService;

	// -------------------Retrieve All InterfaceAccountInfos---------------------------------------------

	@RequestMapping(value = "/map/{type}/{snapshotDate}", method = RequestMethod.GET)
	public ResponseEntity listAccounts(@PathVariable("type") int type, @PathVariable("snapshotDate") String snapshotDate,
                                                                  HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        switch(type){
            case MapType.FIVE_STAR_NET:
                List<FiveStarVo> fiveStarVos = fiveStarTreeNodeService.convertFiveStarVo(snapshotDate);
                return new ResponseEntity (fiveStarVos, HttpStatus.OK);
            case MapType.QUALIFIEDFIVE_STAR_NET:
                List<QualifiedFiveStarVo> qualifiedFiveStarVos = qualifiedFiveStarTreeNodeService.convertQualifiedFiveStarVo(snapshotDate);
                return new ResponseEntity (qualifiedFiveStarVos, HttpStatus.OK);
            case MapType.GOLDDIAMOND_NET:
                List<GoldDiamonndVo> goldDiamonndVos = goldDiamondTreeNodeService.convertGoldDiamondVo(snapshotDate);
                return new ResponseEntity (goldDiamonndVos, HttpStatus.OK);
            case MapType.DOUBLE_GOLDDIAMOND_NET:
                List<DoubleGoldDiamonndVo> doubleGoldDiamonndVos = doubleGoldDiamondTreeNodeService.convertDoubleGoldDiamondVo(snapshotDate);
                return new ResponseEntity (doubleGoldDiamonndVos, HttpStatus.OK);

            case MapType.SIMPLE_NET:
                List<SimpleVo> simpleVos = simpleTreeNodeService.convertSimpleVo(snapshotDate);
                return new ResponseEntity (simpleVos, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

}