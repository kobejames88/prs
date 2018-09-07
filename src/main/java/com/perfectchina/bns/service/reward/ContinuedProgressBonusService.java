package com.perfectchina.bns.service.reward;

/**
 * @Author: chenhuahai
 * @Date: 2018/9/6
 * @Desc: 持续进步奖
 */

public interface ContinuedProgressBonusService {

    void calculateContinuedProgressBonus(String snapshotDate,int quarter);
}
