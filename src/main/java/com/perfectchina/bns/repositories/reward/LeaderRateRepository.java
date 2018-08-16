package com.perfectchina.bns.repositories.reward;

import com.perfectchina.bns.model.reward.LeaderBonusRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/14
 * @Desc:
 */
@Repository
public interface LeaderRateRepository extends JpaRepository<LeaderBonusRate,Long> {

    //find bonus rate by checkAsAtDate
    @Query("SELECT br FROM LeaderBonusRate br WHERE  ( :checkAsAtDate between br.effectiveFrom and br.effectiveTo )")
    List<LeaderBonusRate> findBonusRate(
            @Param("checkAsAtDate") Date checkAsAtDate);

}
