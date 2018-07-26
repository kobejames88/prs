package com.perfectchina.bns.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.perfectchina.bns.model.InterfaceAccountInfo;

@Repository
public interface InterfaceAccountInfoRepository extends JpaRepository<InterfaceAccountInfo, Long> {
	 
	@Query("SELECT ai FROM InterfaceAccountInfo ai WHERE ai.actionDate BETWEEN :dateFrom AND :dateTo order by ai.id ")
	public List<InterfaceAccountInfo> findByActionDate(
			@Param("dateFrom") Date dateFrom,			
			@Param("dateTo") Date dateTo);

	@Query("SELECT ai FROM InterfaceAccountInfo ai WHERE ai.joinDate BETWEEN :dateFrom AND :dateTo order by ai.id ")
	public List<InterfaceAccountInfo> findByJoinDate(
			@Param("dateFrom") Date dateFrom,			
			@Param("dateTo") Date dateTo);
	
	@Query("SELECT ai FROM InterfaceAccountInfo ai WHERE ai.requestStatus = :requestStatus order by ai.id ")
	public List<InterfaceAccountInfo> findByRequestStatus(
			@Param("requestStatus") String requestStatus);	

	@Query("SELECT ai FROM InterfaceAccountInfo ai WHERE ai.accountNum = :accountNum order by ai.id ")
	public List<InterfaceAccountInfo> findByAccountNum(
			@Param("accountNum") String accountNum);	

}
