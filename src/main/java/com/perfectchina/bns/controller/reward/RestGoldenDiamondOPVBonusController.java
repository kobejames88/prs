package com.perfectchina.bns.controller.reward;

import com.perfectchina.bns.model.reward.GoldenDiamondOPVBonus;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.service.reward.GoldenDiamondOPVBonusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/27
 * @Desc: 金钻平级奖
 */
@RestController
@RequestMapping("/api/reward")
public class RestGoldenDiamondOPVBonusController {

    @Autowired
    GoldenDiamondOPVBonusService goldenDiamondOPVBonusService;

    /**
     * calculate goldenDiamondOPVBonus
     * @param snapshotDate
     * @return
     */
    @GetMapping(value = "/calculate/goldendiamondopv/bonus/{snapshotDate}")
    public ResponseEntity<?> createRewardNet(@PathVariable("snapshotDate") String snapshotDate) {

        goldenDiamondOPVBonusService.calculateBonus(snapshotDate);

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    /**
     * list all  bonus  information
     * @return
     */
    @GetMapping(value = "/list/goldendiamondopv/bonus/{snapshotDate}")
    public ResponseEntity<List<GoldenDiamondOPVBonus>> listAccounts(@PathVariable("snapshotDate") String snapshotDate) {

        List<GoldenDiamondOPVBonus> goldenDiamondOPVBonusList =  goldenDiamondOPVBonusService.listBonusInfo(snapshotDate);

        return new ResponseEntity<List<GoldenDiamondOPVBonus>>(goldenDiamondOPVBonusList, HttpStatus.OK);
    }

}
