package com.perfectchina.bns.service;

import java.util.Date;

import com.perfectchina.bns.model.AccountAward;

public interface AccountAwardService {


	// This one check against the latest account award to see if the checkAsDate already pass qualifedTo date.
	public boolean isAccountAwardExpired(long accountId, Date checkAsDate ) ;

	public AccountAward getLatestValidAccountAward(long accountId, Date checkAsDate);
	
	public AccountAward save(AccountAward accountAward);
	
}
