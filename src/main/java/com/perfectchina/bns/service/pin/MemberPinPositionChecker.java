package com.perfectchina.bns.service.pin;

import java.util.Date;
import java.util.List;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.PinPosition;
import com.perfectchina.bns.model.treenode.OpvNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;


/**
 * This checker will be used if the current Pin is New Supervisor
 * @author Steve
 *
 */
public class MemberPinPositionChecker extends AbstractPinPositionChecker {

	private String checkManagerGarde(Account account ) {
		String grade = PinPosition.MEMBER;
		
		return grade;
	}
	
	@Override
	public CheckPinResult checkPinPromotion(TreeNode treeNode, Date lastMonthEndDate) {
		logger.debug("checkPinPromotion, treeNode=["+treeNode+"], lastMonthEndDate="+lastMonthEndDate );
		
		CheckMeetTargetResult result = checkMeetQualifiedFiveStar( (OpvNetTreeNode) treeNode, lastMonthEndDate);
		
		CheckPinResult resultPin = new CheckPinResult();
		resultPin.setOldPin( treeNode.getData().getPin() );		
		
		if ( result.isMeetQualifiedFiveStar()  ) { 
			resultPin.setPinStatusChanged(true);
			resultPin.setNewPin( treeNode.getData().getPin() ); // pin no changes
			resultPin.setQualifiedFrom( result.getLastDateMeetQualifiedFiveStar() );
		}
		
		logger.debug("checkPinPromotion, treeNode=["+treeNode+"], CheckPinResult=["+resultPin+"]");
		return resultPin;
	}

	@Override
	public String checkPinReQualification(Account account, Date lastMonthEndDate) {
		logger.debug("checkPinReQualification, account=["+account+"], lastMonthEndDate="+ lastMonthEndDate );
		
		String resultPin = account.getPin();

		
		logger.debug("checkPinReQualification, resultPin=["+resultPin+"]");
		return resultPin;
	}
	
	

}
