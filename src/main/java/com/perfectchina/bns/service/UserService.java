package com.perfectchina.bns.service;


import java.util.List;

import com.perfectchina.bns.model.User;

public interface UserService {
	
	User findById(Long id);

	User findByUserName(String username);

	void saveUser(User user);

	void updateUser(User user);

	void deleteUserById(Long id);

	void deleteAllUsers();

	List<User> findAllUsers();

	boolean isUserExist(User user);
}