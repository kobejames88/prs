package com.perfectchina.bns.repositories;


import com.perfectchina.bns.model.MemberLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * creat by xb
 */

@Repository
public interface MemberLogRepositories extends JpaRepository<MemberLog,Integer> {


    @Query("select a from MemberLog a where a.serviceCenterNum=:serviceCenterNum")
    MemberLog findDate(@Param("serviceCenterNum") String serviceCenterNum);

}
