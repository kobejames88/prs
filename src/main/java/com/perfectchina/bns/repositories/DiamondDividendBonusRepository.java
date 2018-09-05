package com.perfectchina.bns.repositories;

import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.reward.DiamondDividendBonusReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DiamondDividendBonusRepository extends JpaRepository<DiamondDividendBonusReward, Long> {
	@Query("SELECT a FROM DiamondDividendBonusReward a WHERE DATE_FORMAT(a.creationDate , '%Y%m')= :requireDate")
	public List<DiamondDividendBonusReward> getDiamondDividendBonusRewardsByCreationDate(
			@Param("requireDate") String requireDate);

    @Query("SELECT a FROM DiamondDividendBonusReward a WHERE a.account = :account")
    public DiamondDividendBonusReward getDiamondDividendBonusRewardByAccount(@Param("account") Account account);
}