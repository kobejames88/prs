package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.model.reward.GoldenDiamondOPVBonus;
import java.util.List;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/27
 * @Desc: 金钻平级奖
 */

public interface GoldenDiamondOPVBonusService {
    void calculateBonus(String snapshotDate);

    List<GoldenDiamondOPVBonus> listBonusInfo(String snapshotDate);
}
