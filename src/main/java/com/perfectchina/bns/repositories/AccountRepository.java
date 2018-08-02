package com.perfectchina.bns.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.perfectchina.bns.model.Account;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	

	@Query("SELECT a FROM Account a WHERE a.accountNum = :accountNum order by a.id desc")
	public Account getAccountByAccountNum(@Param("accountNum") String accountNum);
	
	@Query("SELECT a FROM Account a WHERE DATE_FORMAT(a.promotionDate , '%Y-%m')= :requireDate AND a.status = 'A' order by a.id")
	public List<Account> getAccountByPromotionDateYearMonth(
			@Param("requireDate") String requireDate);
			
	@Query("SELECT distinct a FROM Account a WHERE a.promotionDate BETWEEN :dateFrom AND :dateTo AND a.status = 'A' order by a.id")
	public List<Account> getAccountByPromotionDate(
			@Param("dateFrom") Date dateFrom,			
			@Param("dateTo") Date dateTo);
	
//	@Query("SELECT a FROM Account a WHERE a.id = :id AND a.status = 'A'")
	@Query("SELECT a FROM Account a WHERE a.id = :id ")
	public Account getAccountById(@Param("id") Long id);
	
	@Query("SELECT a FROM Account a WHERE a.accountNum = :accountNum AND a.status = 'A'")
	public Account getAccountById(@Param("accountNum") String accountNum);
	
	@Query("SELECT a FROM Account a WHERE a.accountNum = :accountNum AND a.status = 'A' AND DATE_FORMAT(a.promotionDate , '%Y-%m')= :requireDate")
	public Account getAccountByAccountNumAndRequireDate(
			@Param("accountNum") String accountNum,
			@Param("requireDate") String requireDate);

	

}