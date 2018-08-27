package com.perfectchina.bns.repositories.reward;

import com.perfectchina.bns.model.reward.GoldenDiamondOPVBonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/23
 * @Desc: 金钻评级奖
 */
@Repository
public interface GoldenDiamondOPVBonusRepository extends JpaRepository<GoldenDiamondOPVBonus,Long> {

    List<GoldenDiamondOPVBonus> findBySnapshotDate(String snapshotDate);

}
