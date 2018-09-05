package com.perfectchina.bns.controller.reward;

import com.perfectchina.bns.model.reward.GoldenDiamondOPVBonus;
import com.perfectchina.bns.service.reward.DiamondDividendBonusService;
import com.perfectchina.bns.service.reward.GoldenDiamondOPVBonusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: TerryTang
 * @Date: 2018/9/4
 * @Desc: 钻石分红奖
 */
@RestController
@RequestMapping("/api/reward")
public class RestDiamondDividendBonusController {

    @Autowired
    DiamondDividendBonusService diamondDividendBonusService;

    /**
     * calculate goldenDiamondOPVBonus
     * @param
     * @return
     */
    @GetMapping(value = "/diamondDividendBonus")
    public ResponseEntity<?> createRewardNet() {

        diamondDividendBonusService.calculateTotalIntegral();
        diamondDividendBonusService.calculateDividendBonus();

        return new ResponseEntity<String>(HttpStatus.OK);
    }

}
