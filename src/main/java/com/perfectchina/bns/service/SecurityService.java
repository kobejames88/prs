package com.perfectchina.bns.service;

public interface SecurityService {
	public String findLoggedInUsername();
	public void autologin(String username, String password);
	
}
