package com.perfectchina.bns.repositories.reward;

import com.perfectchina.bns.model.reward.DoubleDiamondBonusTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/29
 * @Desc: 双金钻奖模拟测试数据
 */
@Repository
public interface DoubleDiamondBonusTestRepository extends JpaRepository<DoubleDiamondBonusTest,Long> {

    @Query("SELECT a FROM DoubleDiamondBonusTest a WHERE a.snapshotDate = :snapShotDate and a.account.id = :AccountId")
    DoubleDiamondBonusTest findByAccountNum(@Param("snapShotDate") String snapShotDate, @Param("AccountId") Long AccountId);

}
