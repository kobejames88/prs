package com.perfectchina.bns.repositories;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.perfectchina.bns.model.InterfaceSalesRecordInfo;


@Repository
public interface InterfaceSalesRecordInfoRepository extends JpaRepository<InterfaceSalesRecordInfo, Long> {
	
	@Query("SELECT ai FROM InterfaceSalesRecordInfo ai WHERE ai.requestDate = :requestDate order by ai.id ")
	public List<InterfaceSalesRecordInfo> findByRequestDate(
			@Param("requestDate") Date requestDate);

	@Query("SELECT ai FROM InterfaceSalesRecordInfo ai WHERE ai.salesDate BETWEEN :dateFrom AND :dateTo order by ai.id ")
	public List<InterfaceSalesRecordInfo> findBySalesDate(
			@Param("dateFrom") Date dateFrom,			
			@Param("dateTo") Date dateTo);
	
	@Query("SELECT ai FROM InterfaceSalesRecordInfo ai WHERE ai.requestStatus = :requestStatus order by ai.id ")
	public List<InterfaceSalesRecordInfo> findByRequestStatus(
			@Param("requestStatus") String requestStatus);
		
}
