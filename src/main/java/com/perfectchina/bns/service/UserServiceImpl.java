package com.perfectchina.bns.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.perfectchina.bns.model.User;
import com.perfectchina.bns.repositories.RoleRepository;
import com.perfectchina.bns.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
	public User findById(Long id) {
		return userRepository.findOne(id);
	}

	public User findByUserName(String username) {
		return userRepository.findByUserName(username);
	}

	public void saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        // default to all the roles
        user.setRoles(new HashSet<>(roleRepository.findAll())); 
        userRepository.save(user);
	}

	public void updateUser(User user){
		saveUser(user);
	}

	public void deleteUserById(Long id){
		userRepository.delete(id);
	}

	public void deleteAllUsers(){
		userRepository.deleteAll();
	}

	public List<User> findAllUsers(){
		return userRepository.findAll();
	}

	public boolean isUserExist(User user) {
		return findByUserName(user.getUsername()) != null;
	}


}
