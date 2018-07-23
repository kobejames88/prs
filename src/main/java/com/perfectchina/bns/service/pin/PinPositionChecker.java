package com.perfectchina.bns.service.pin;

import java.util.Date;

import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.service.AccountAwardService;
import com.perfectchina.bns.service.AccountService;


public interface PinPositionChecker {


	// check if the account can promote be promoted, e.g. TRAINEE to ASSOCIATE_SUPERVISOR, ASSOCIATE_SUPERVISOR to DIAMOND
	// It suppose to be run on every month end.
	public CheckPinResult checkPinPromotion(TreeNode treeNode, Date lastMonthEndDate);
	
	// check if the account can be state as original pin position or downgrade one post level
	// It suppose to be run on Fiscal Year Month End (ie. March of each year) 
	// It should be run only after checkPinPromtion and reward calculation so that the next run on checkPinPromotion is correct 
	public String checkPinReQualification(Account account, Date lastMonthEndDate);
	
	
	public void setAccountService(AccountService accountService);
	public void setAccountAwardService(AccountAwardService accountAwardService);

}
