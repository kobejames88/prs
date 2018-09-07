package com.perfectchina.bns.controller.reward;

import com.perfectchina.bns.service.pin.PinPosition;
import com.perfectchina.bns.service.reward.DividendBonus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author: TerryTang
 * @Date: 2018/9/4
 * @Desc: 钻石分红奖
 */
@RestController
@RequestMapping("/api/reward")
public class RestDiamondDividendBonusController {

    @Autowired
    DividendBonus dividendBonus;

    /**
     * calculate goldenDiamondOPVBonus
     * @param
     * @return
     */
    @GetMapping(value = "/diamondDividendBonus")
    public ResponseEntity<?> createRewardNet() {

        dividendBonus.build(PinPosition.DIAMOND);

        return new ResponseEntity<String>(HttpStatus.OK);
    }

}
