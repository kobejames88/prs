package com.perfectchina.bns.service.pin;

import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.service.AccountAwardService;
import com.perfectchina.bns.service.AccountService;

public class PinPositionCheckerFactory {

	/**
	 * This class create the relevant PinPositionChecker to find out the newest position based on their position
	 * @param account
	 * @param accountAwardService TODO
	 * @return
	 */
	public static PinPositionChecker create(Account account, AccountService accountService, AccountAwardService accountAwardService) {
		if ( account == null ) {
			throw new IllegalArgumentException("account is null");
		}		
		PinPositionChecker checker = null;
		if ( PinPosition.MEMBER.equals( account.getPin() )) {
			checker = new MemberPinPositionChecker();
			/*
		} else if ( PinPosition.DIAMOND.equals( account.getPin() )) {
			checker = new SupervisorPinPositionChecker();
		} else if ( PinPosition.DIAMOND.equals( account.getPin() )) {
			checker = new SupervisorPinPositionChecker();
		} else if ( PinPosition.GOLD_DIAMOND.equals( account.getPin() )) {
			checker = new ManagerPinPositionChecker();
		} else if ( PinPosition.DOUBLE_GOLD_DIAMOND.equals( account.getPin() )) {
			checker = new SeniorManagerPinPositionChecker();
		} else if ( PinPosition.TRIPLE_GOLD_DIAMOND.equals( account.getPin() )) {
			checker = new ExecutiveManagerPinPositionChecker();
			*/
		} else {
			throw new IllegalArgumentException("Illegal account pin ["+ account.getPin()+"].");
			
		}
		
		
		checker.setAccountService(accountService);
		checker.setAccountAwardService(accountAwardService);
		
		return checker;
	}
}
