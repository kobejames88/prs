package com.perfectchina.bns.service;

import java.util.List;

import com.perfectchina.bns.model.Account;


public interface AccountService {

	public String getNameById(long accountId);
	
	public Account getAccountbyAccountNum(String accountNum);
	
	public List<Account> retrieveDirectChild(long accountId);
	
	public List<Account> retrieveDirectVIP(long accountId);

	public List<Account> findAccountChildLeafList();
	
	public List<Account> findDistributorChildList(Long parentId);
	
	public List<Account> findDistributorChildLeafList();
	
	public List<Account> findDistributorAccountAtLevel(int levelNum);
	
	public Account save(Account account);
	
	public Account getOne(long accountId);
}
