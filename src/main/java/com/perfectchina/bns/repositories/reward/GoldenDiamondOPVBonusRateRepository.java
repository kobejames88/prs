package com.perfectchina.bns.repositories.reward;

import com.perfectchina.bns.model.reward.GoldenDiamondOPVBonusRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/23
 * @Desc: 金钻评级奖计算比率
 */
@Repository
public interface GoldenDiamondOPVBonusRateRepository extends JpaRepository<GoldenDiamondOPVBonusRate,Long> {

    @Query("SELECT br FROM GoldenDiamondOPVBonusRate br WHERE  ( :checkAsAtDate between br.effectiveFrom and br.effectiveTo )  ")
    List<GoldenDiamondOPVBonusRate> findBonusRateByDate(
            @Param("checkAsAtDate") Date checkAsAtDate);

}
