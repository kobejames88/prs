package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.model.reward.TripleGoldenDiamondBonus;

import java.util.List;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/29
 * @Desc: 三金钻奖
 */

public interface TripleGoldenDiamondBonusService {
    //计算三金钻奖
    void calculateBonus(String snapshotDate);

    //获取所有三金钻奖信息
    List<TripleGoldenDiamondBonus> listBonusInfo(String snapshotDate);
}
