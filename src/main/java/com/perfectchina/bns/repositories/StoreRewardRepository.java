package com.perfectchina.bns.repositories;

import com.perfectchina.bns.model.reward.StoreReward;
import org.springframework.boot.web.servlet.server.Jsp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * creat by xb
 */
@Repository
public interface StoreRewardRepository extends JpaRepository<StoreReward,Integer> {
/*    //时间，会员账号
    @Query("select b from StoreReward b where b.accountNum=:accountNum and  b.rewardDate=:date ")
    List<StoreReward> findStoreReward(@Param("accountNum") String accountNum,@Param("date") String date);*/
    @Query("select b from StoreReward b where b.serviceCenterNum=:serviceCenterNum and DATE_FORMAT(b.rewardDate, '%Y-%m')=:date ")
    StoreReward findId(@Param("serviceCenterNum") String serviceCenterNum, @Param("date") String date);

    @Query("select b from StoreReward b where DATE_FORMAT(b.rewardDate, '%Y-%m')=:date ")
    List<StoreReward> findInfo(@Param("date") String date);
}

