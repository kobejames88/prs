package com.perfectchina.bns.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.perfectchina.bns.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserName(String username);

}
