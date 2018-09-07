package com.perfectchina.bns.controller.reward;

import com.perfectchina.bns.service.reward.ContinuedProgressBonusService;
import com.perfectchina.bns.service.reward.ContinuedProgressRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: chenhuahai
 * @Date: 2018/9/6
 * @Desc: 持续进步奖
 */
@RestController
@RequestMapping("/api/reward")
public class RestApiContinuedProgressBonusController {

    @Autowired
    private ContinuedProgressRecordService continuedProgressRecordService;

    @Autowired
    private ContinuedProgressBonusService continuedProgressBonusService;

    /**
     * 计算持续进步得分
     * @param snapshotDate yyyyMM
     * @return
     */
    @GetMapping(value = "/calculate/continuedprogress/record/{snapshotDate}")
    public ResponseEntity<?> calculatePoint(@PathVariable("snapshotDate") String snapshotDate) {

        continuedProgressRecordService.calculateProgressRecord(snapshotDate);

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    /**
     * 计算持续进步奖
     * snapshotDate yyyy-MM-MM-MM
     * @param snapshotDate
     * @return
     */
    @GetMapping(value = "/calculate/continuedprogress/bonus/{snapshotDate}/{quarter}")
    public ResponseEntity<?> calculateBonus(@PathVariable("snapshotDate") String snapshotDate,@PathVariable("quarter")Integer quarter) {

        continuedProgressBonusService.calculateContinuedProgressBonus(snapshotDate,quarter);

        return new ResponseEntity<String>(HttpStatus.OK);
    }

}
