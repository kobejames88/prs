package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.model.reward.GoldenDiamondOPVBonus;
import java.util.List;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/27
 * @Desc: 金钻平级奖
 */

public interface GoldenDiamondOPVBonusService {
    //计算金钻平级奖
    void calculateBonus(String snapshotDate);

    //获取所有金钻平级奖信息
    List<GoldenDiamondOPVBonus> listBonusInfo(String snapshotDate);
}
