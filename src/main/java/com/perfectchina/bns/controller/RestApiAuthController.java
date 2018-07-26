package com.perfectchina.bns.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.perfectchina.bns.model.User;
import com.perfectchina.bns.service.UserService;
import com.perfectchina.bns.util.CustomErrorType;


@RestController
@RequestMapping("/auth")
public class RestApiAuthController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiAuthController.class);

	@Autowired
	UserService userService; //Service which will do all data retrieval/manipulation work

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder; // for authenticate
	

	// ---- Authenticate user --- 
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<User> login(@RequestBody User user) {
		logger.info("Authenticate user:"+ user);
		
		// logger.debug("users="+userService.findAllUsers() );
		
		User dbUser = userService.findByUserName( user.getUsername() );

		if (dbUser != null) {
			logger.debug("Start authenticate User with username [{}].", user.getUsername() );
			//logger.debug("dbUser.getPassword() = " + dbUser.getPassword() );
			//logger.debug("user.getPassword() = " + user.getPassword() );
			if ( dbUser.getPassword() != null ) {
				//String encryptedPasword = bCryptPasswordEncoder.encode( user.getPassword() );
				//logger.debug("encryptedPasword = " + encryptedPasword );
				if ( bCryptPasswordEncoder.matches( user.getPassword(), dbUser.getPassword())) {				
				//if ( dbUser.getPassword().equals( encryptedPasword ) ) { // this won't work, not same
					logger.info("User with username {} authenticated.", user.getUsername() );
					return new ResponseEntity<User>(dbUser, HttpStatus.OK);					
				}
			}
		}
			
		logger.warn("Unable to authenticate. Username [{}] or password incorrect.", user.getUsername() );
		// json client side not handled status 401 with message pass
		return new ResponseEntity(new CustomErrorType("Unable to authenticate. Username or password incorrect."),
				HttpStatus.UNAUTHORIZED);
		
		//return new ResponseEntity(new CustomErrorType("Unable to authenticate. Username or password incorrect."),
		//		HttpStatus.OK);
		
		
	}
    
	
	
}