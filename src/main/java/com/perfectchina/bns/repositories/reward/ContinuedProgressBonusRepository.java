package com.perfectchina.bns.repositories.reward;

import com.perfectchina.bns.model.reward.ContinuedProgressBonus;
import com.perfectchina.bns.model.reward.ContinuedProgressRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @Author: chenhuahai
 * @Date: 2018/9/6
 * @Desc: 持续进步奖
 */
@Repository
public interface ContinuedProgressBonusRepository extends JpaRepository<ContinuedProgressBonus,Long>{

    //根据时间和会员id查找
    @Query("SELECT a FROM ContinuedProgressBonus a WHERE a.snapshotDate = :snapShotDate and a.account.id = :AccountId")
    ContinuedProgressBonus findContinuedProgressBonus(@Param("snapShotDate") String snapShotDate, @Param("AccountId") Long AccountId);

}
