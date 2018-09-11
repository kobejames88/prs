package com.perfectchina.bns.repositories.reward;

import com.perfectchina.bns.model.reward.BottomQualifiedFiveStarReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: TerryTang
 * @Date: 2018/9/11 上午11:34
 * @Desc:
 */

@Repository
public interface BottomQualifiedFiveStarRewardRepository extends JpaRepository<BottomQualifiedFiveStarReward,Long> {

}
