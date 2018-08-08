package com.perfectchina.bns.repositories.reward;

import com.perfectchina.bns.model.reward.DistributorBonusRate;
import com.perfectchina.bns.model.reward.DistributorDifferentialBonusRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/7
 * @Desc:
 */
@Repository
public interface DistributorDifferentialBonusRateRepository extends JpaRepository<DistributorDifferentialBonusRate,Long> {

    //find bonus rate by opv
    @Query("SELECT br FROM DistributorDifferentialBonusRate br WHERE (:opv between br.minOpv and br.maxOpv ) and ( :checkAsAtDate between br.effectiveFrom and br.effectiveTo )  ")
    DistributorBonusRate findBonusRateByOpvAndDateAsc(
            @Param("opv") float opv,
            @Param("checkAsAtDate") Date checkAsAtDate);

}
