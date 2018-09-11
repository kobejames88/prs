package com.perfectchina.bns.controller.reward;

import com.perfectchina.bns.model.MemberLog;
import com.perfectchina.bns.model.reward.StoreReward;
import com.perfectchina.bns.service.StoreRewordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * creat by xb
 */
@Api(value = "店铺奖励")
@CrossOrigin
@RestController
@RequestMapping(value = "/api")
public class StoreRewordController {

    @Autowired
    private StoreRewordService storeRewordService;

    @ApiModelProperty(value = "通过报单日期算出奖金")
    @GetMapping(value = "/storeReword")
    public List<StoreReward> calculateStoreReword(@RequestParam(value = "date") String date){
        List<StoreReward> storeRewards=storeRewordService.findInfo(date);
        return storeRewards;
    }

}
