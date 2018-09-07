package com.perfectchina.bns.repositories.reward;

import com.perfectchina.bns.model.reward.TripleGoldenDiamondBonusRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/29
 * @Desc:  三金钻奖计算比率
 */
@Repository
public interface TripleGoldenDiamondBonusRateRepository extends JpaRepository<TripleGoldenDiamondBonusRate,Long> {

    @Query("SELECT br FROM TripleGoldenDiamondBonusRate br WHERE  ( :checkAsAtDate between br.effectiveFrom and br.effectiveTo )  ")
    List<TripleGoldenDiamondBonusRate> findBonusRateByDate(
            @Param("checkAsAtDate") Date checkAsAtDate);

}
