package com.perfectchina.bns.repositories;

import com.perfectchina.bns.model.AccountPin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AccountPinRepository extends JpaRepository<AccountPin, Long> {

	@Query("SELECT sr FROM AccountPin sr WHERE sr.account.id = :accountId and sr.creationDate BETWEEN :fromDate AND :toDate order by sr.creationDate")
	public List<AccountPin> findByAccountIdAndSnapshotDate(
            @Param("accountId") Long accountId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate);

	@Query("SELECT sr FROM AccountPin sr WHERE sr.account.id = :accountId order by sr.creationDate DESC")
	public List<AccountPin> findByAccountIdOrderByPromotionDate(
            @Param("accountId") Long accountId);

	@Query("SELECT sr FROM AccountPin sr WHERE sr.account.id = :accountId and sr.creationDate >= :requireDate order by sr.creationDate")
	public List<AccountPin> findByAccountIdAndRequireDate(
            @Param("accountId") Long accountId,
            @Param("requireDate") String requireDate);
}
