package com.perfectchina.bns.repositories;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.perfectchina.bns.model.SalesRecord;

@Repository
public interface SalesRecordRepository extends JpaRepository<SalesRecord, Long> {
	
	@Query("SELECT sr FROM SalesRecord sr WHERE sr.account.id = :accountId and sr.salesDate BETWEEN :oneMonthBefore AND :currentDate")
	public List<SalesRecord> findByAccountId(
			@Param("accountId") Long accountId,
			@Param("oneMonthBefore") Date oneMonthBefore,
			@Param("currentDate") Date currentDate);

	@Query("SELECT sr FROM SalesRecord sr WHERE sr.orderNum = :orderNum and sr.salesDate = :salesDate ")
	public SalesRecord getByOrderNum(
			@Param("orderNum") String orderNum,
			@Param("salesDate") Date salesDate);

}
