
package com.perfectchina.bns.repositories;


import com.perfectchina.bns.model.reward.StoreRewardsRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


/**
 * creat by xb
 */

@Repository
public interface StoreRewardsRateRepository extends JpaRepository<StoreRewardsRate,Integer> {

    @Query("select b from StoreRewardsRate b")
    public StoreRewardsRate findData();
}

