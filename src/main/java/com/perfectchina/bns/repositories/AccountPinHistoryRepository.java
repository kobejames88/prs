package com.perfectchina.bns.repositories;

import com.perfectchina.bns.model.AccountPinHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AccountPinHistoryRepository extends JpaRepository<AccountPinHistory, Long> {
	
	@Query("SELECT sr FROM AccountPinHistory sr WHERE sr.account.id = :accountId and sr.promotionDate BETWEEN :fromDate AND :toDate")
	public List<AccountPinHistory> findByAccountId(
			@Param("accountId") Long accountId,
			@Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate);
	
	@Query("SELECT sr FROM AccountPinHistory sr WHERE sr.account.id = :accountId order by sr.promotionDate DESC")
	public List<AccountPinHistory> findByAccountIdOrderByPromotionDate(
			@Param("accountId") Long accountId);
	
	@Query("SELECT sr FROM AccountPinHistory sr WHERE sr.account.id = :accountId and sr.promotionDate >= :requireDate order by sr.promotionDate")
	public List<AccountPinHistory> findByAccountIdAndRequireDate(
			@Param("accountId") Long accountId,
			@Param("requireDate") Date requireDate);
}
