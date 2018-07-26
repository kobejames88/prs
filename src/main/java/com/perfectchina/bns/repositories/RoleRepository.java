package com.perfectchina.bns.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.perfectchina.bns.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
}