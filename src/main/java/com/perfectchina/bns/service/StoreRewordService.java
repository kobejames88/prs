package com.perfectchina.bns.service;

import com.perfectchina.bns.model.SalesRecord;
import com.perfectchina.bns.model.reward.StoreReward;

import java.util.List;

/**
 * creat by xb
 */
public interface StoreRewordService{

    //还要加个时间
    void calculateBonus(String date);
   /* SalesRecord findAll(String accountNum);*/
   List<StoreReward> findInfo(String date);
}
