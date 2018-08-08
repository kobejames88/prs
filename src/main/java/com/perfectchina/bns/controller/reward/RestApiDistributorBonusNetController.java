package com.perfectchina.bns.controller.reward;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.service.reward.DistributorBonusNetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/8
 * @Desc:
 */
@RestController
@RequestMapping("/api/reward")
public class RestApiDistributorBonusNetController {

    public static final Logger logger = LoggerFactory.getLogger(RestApiDistributorBonusNetController.class);

    @Autowired
    private DistributorBonusNetService distributorBonusNetService;


    /**
     * create distributorBonusNet
     * @return
     */
    @GetMapping(value = "/create/distributor/bonusnet")
    public ResponseEntity<?> createRewardNet() {

        distributorBonusNetService.createRewardNet();

        return new ResponseEntity<String>(HttpStatus.OK);
    }


}
