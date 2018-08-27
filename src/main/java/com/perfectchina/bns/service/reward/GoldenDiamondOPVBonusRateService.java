package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.model.reward.GoldenDiamondOPVBonusRate;

import java.util.Date;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/27
 * @Desc: 金钻平级奖计算比率
 */

public interface GoldenDiamondOPVBonusRateService {

    GoldenDiamondOPVBonusRate findBonusRateByDate(Date checkAsAtDate);
}
