package com.perfectchina.bns.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.perfectchina.bns.model.CustomerBonusRate;

@Repository
public interface CustomerBonusRateRepository extends JpaRepository<CustomerBonusRate, Long> {
	
	@Query("SELECT br FROM CustomerBonusRate br WHERE br.aopv <= :aopv and ( :checkAsAtDate between br.effectiveFrom and br.effectiveTo ) order by br.aopv desc ")
	List<CustomerBonusRate> findBonusRateByAopvAndDateDesc(
            @Param("aopv") float aopv,
            @Param("checkAsAtDate") Date checkAsAtDate);

	//find all rate >= lastMonth rate base on LMAOPV asc
	@Query("SELECT br FROM CustomerBonusRate br WHERE br.aopv > :aopv and ( :checkAsAtDate between br.effectiveFrom and br.effectiveTo ) order by br.aopv asc ")
	List<CustomerBonusRate> findBonusRateByAopvAndDateAsc(
			@Param("aopv") float aopv,
			@Param("checkAsAtDate") Date checkAsAtDate);

}
