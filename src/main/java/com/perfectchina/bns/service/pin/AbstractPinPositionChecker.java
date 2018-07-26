package com.perfectchina.bns.service.pin;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.perfectchina.bns.model.treenode.OpvNetTreeNode;
import com.perfectchina.bns.service.AccountAwardService;
import com.perfectchina.bns.service.AccountService;
import com.perfectchina.bns.service.OpvTreeNodeService;

public abstract class AbstractPinPositionChecker implements PinPositionChecker {

	protected final Logger logger = LoggerFactory.getLogger( getClass() );
	
	private OpvTreeNodeService opvTreeNodeService;
	
	private AccountService accountService;
	private AccountAwardService accountAwardService;
	

	public AccountAwardService getAccountAwardService() {
		return accountAwardService;
	}
	public void setAccountAwardService(AccountAwardService accountAwardService) {
		this.accountAwardService = accountAwardService;
	}

	
	
	public OpvTreeNodeService getOpvTreeNodeService() {
		return opvTreeNodeService;
	}
	public void setOpvTreeNodeService(OpvTreeNodeService opvTreeNodeService) {
		this.opvTreeNodeService = opvTreeNodeService;
	}
	public AccountService getAccountService() {
		return accountService;
	}
	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
		

	/**
	 * If OPV >= 36000 ( thresholdOpv), it is qualified 5 star
	 * Or ( if OPV >= 18000 and OPV_LastMonth >= 18000 ), it is also qualify
	 * @param opvNetTreeNode
	 * @param thresholdAopvLastMonth
	 * @param thresholdOpv
	 * @return
	 */
	public CheckMeetTargetResult checkMeetQualifiedFiveStar(OpvNetTreeNode opvNetTreeNode, Date lastMonthEndDate ) {
		
		// List<AccountReward> monthlyRewards = accountRewardService.findAccountRewardByAccountId(accountId, RewardType.STANDARD, dateFrom, dateTo);
		CheckMeetTargetResult result = new CheckMeetTargetResult();
		if ( opvNetTreeNode.getOpv() >= 36000F ) {
			result.setMeetQualifiedFiveStar( true );
			result.setLastDateMeetQualifiedFiveStar( lastMonthEndDate );
		} else {
			if ( ( opvNetTreeNode.getOpv() >= 18000F) && ( opvNetTreeNode.getAopvLastMonth() >= 18000F ) ) {
				result.setMeetQualifiedFiveStar( true );
				result.setLastDateMeetQualifiedFiveStar( lastMonthEndDate );
			}
		}
		logger.debug("checkMeetQualifiedFiveStar, result=["+result+"]");
		return result;
	}
}
