package com.perfectchina.bns.repositories.reward;

import com.perfectchina.bns.model.CustomerBonusRate;
import com.perfectchina.bns.model.reward.DistributorBonusRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/7
 * @Desc:
 */
@Repository
public interface DistributorBonusRateRepository extends JpaRepository<DistributorBonusRate,Long>{


    //find bonus rate by gpv
    @Query("SELECT br FROM DistributorBonusRate br WHERE (:gpv between br.minGpv and br.maxGpv ) and ( :checkAsAtDate between br.effectiveFrom and br.effectiveTo )")
    DistributorBonusRate findBonusRateByGpvAndDate(
            @Param("gpv") float gpv,
            @Param("checkAsAtDate") Date checkAsAtDate);
}
