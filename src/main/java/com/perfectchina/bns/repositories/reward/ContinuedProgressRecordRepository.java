package com.perfectchina.bns.repositories.reward;

import com.perfectchina.bns.model.reward.ContinuedProgressRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @Author: chenhuahai
 * @Date: 2018/9/5
 * @Desc: 持续进步得分记录
 */
@Repository
public interface ContinuedProgressRecordRepository extends JpaRepository<ContinuedProgressRecord,Long>{


    //根据时间和会员id查找得分记录
    @Query("SELECT a FROM ContinuedProgressRecord a WHERE a.snapshotDate = :snapShotDate and a.account.id = :AccountId")
    ContinuedProgressRecord findByAccountNum(@Param("snapShotDate") String snapShotDate, @Param("AccountId") Long AccountId);

}
