package com.perfectchina.bns.repositories.reward;

import com.perfectchina.bns.model.reward.TripleGoldenDiamondBonus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/29
 * @Desc: 三金钻奖
 */

public interface TripleGoldenDiamondBonusRepository extends JpaRepository<TripleGoldenDiamondBonus,Long> {

    List<TripleGoldenDiamondBonus> findBySnapshotDate(String snapshotDate);

}
