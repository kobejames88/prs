package com.perfectchina.bns.controller.reward;

import com.perfectchina.bns.model.reward.GoldenDiamondOPVBonus;
import com.perfectchina.bns.model.reward.TripleGoldenDiamondBonus;
import com.perfectchina.bns.service.reward.TripleGoldenDiamondBonusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/29
 * @Desc: 金钻平级奖
 */
@RestController
@RequestMapping("/api/reward")
public class RestApiTripleGoldenDiamondBonusController {

    @Autowired
    private TripleGoldenDiamondBonusService tripleGoldenDiamondBonusService;

    /**
     *  计算三金钻奖
     * @param snapshotDate
     * @return
     */
    @GetMapping(value = "/calculate/triplediamond/bonus/{snapshotDate}")
    public ResponseEntity<?> calculateBonus(@PathVariable("snapshotDate") String snapshotDate) {

        tripleGoldenDiamondBonusService.calculateBonus(snapshotDate);

        return new ResponseEntity<String>(HttpStatus.OK);
    }


    /**
     * 根据时间获取三金钻奖信息
     * @param snapshotDate
     * @return
     */
    @GetMapping(value = "/list/triplediamond/bonus/{snapshotDate}")
    public ResponseEntity<List<TripleGoldenDiamondBonus>> listAccounts(@PathVariable("snapshotDate") String snapshotDate) {

        List<TripleGoldenDiamondBonus> goldenDiamondOPVBonusList =  tripleGoldenDiamondBonusService.listBonusInfo(snapshotDate);

        return new ResponseEntity<List<TripleGoldenDiamondBonus>>(goldenDiamondOPVBonusList, HttpStatus.OK);
    }
}
