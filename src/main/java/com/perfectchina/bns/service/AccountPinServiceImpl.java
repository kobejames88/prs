package com.perfectchina.bns.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.AccountAward;
import com.perfectchina.bns.model.AccountPinHistory;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.AccountPinHistoryRepository;
import com.perfectchina.bns.service.pin.CheckPinResult;
import com.perfectchina.bns.service.pin.PinPositionChecker;
import com.perfectchina.bns.service.pin.PinPositionCheckerFactory;

@Transactional
@Service
public class AccountPinServiceImpl implements AccountPinService {
		
	private final Logger logger = LoggerFactory.getLogger( this.getClass() );

	@Autowired
	private SimpleTreeNodeService simpleTreeNodeService;
	
	/*
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private AccountPinHistoryRepository accountPinHistoryRepository;
	
	@Autowired
	private SalesRecordService salesRecordService;
	
	@Autowired
	private AccountAwardService accountAwardService;
	*/
	
	/**
	 * This function return the new account pin based on the GPV
	 * @param account account to check
	 * @return the new pin for the account based on the GPV
	 */
	private CheckPinResult checkAccountPinPromotion(TreeNode treeNode, Date lastMonthEndDate) {
		logger.debug("checkAccountPinPromotion, treeNode=["+treeNode+"]");		
		//PinPositionChecker checker = PinPositionCheckerFactory.create(treeNode.getData(), accountService, accountAwardService);
		//CheckPinResult resultPin = checker.checkPinPromotion(treeNode, lastMonthEndDate);
		//logger.debug("checkAccountPinPromotion, resultPin=["+resultPin+"]");		
		//return resultPin;
		throw new IllegalArgumentException("Not yet implemented.");
	}	
	
	public void accountPinPromotion(Date lastMonthEndDate){
		logger.info("accountPinPromotion, start with date ["+ lastMonthEndDate +"].");
		
		// List<SimpleNetTreeNode> childList = simpleTreeNodeService.f
		/*
		List<Account> childList = accountService.findAccountChildLeafList();
		int maxTreeLevel = 0;
		
		// loop for the children to calculate PPV at the lowest level
		for (Account child: childList) {
			long accountId =  child.getId();
			//logger.debug( "accountPinPromotion, accountId=["+accountId+"]");
			logger.info( "accountPinPromotion, accountNum=["+child.getAccountNum()+"]");

			if ( child.getLevelNum() > maxTreeLevel ) {
				maxTreeLevel = child.getLevelNum(); 
				logger.debug( "accountPinPromotion, temp maxTreeLevel=["+maxTreeLevel+"]");
			}
			
			
			List<SalesRecord> accountMonthlySales = salesRecordService.retrieveSelfAndVIPSaleRecordOfLastMonth( accountId, lastMonthEndDate );
			SalesRecord totalSales = salesRecordService.findOutPersonalSalesRecordTotal(accountMonthlySales);
			
			CheckPinResult checkPinResult = checkAccountPinPromotion(child, lastMonthEndDate);
			logger.debug( "accountPinPromotion, check as at date ["+ lastMonthEndDate+"], accountPin=["+ child.getPin()+"], checkPinResult=["+ checkPinResult+"]");				

			// update Pin to database if pin changed 
			if ( checkPinResult.isPinStatusChanged() ) {
				storeNewAccountPin(lastMonthEndDate, child, checkPinResult );				
			}			
		} // end for loop

		// can check the maxTreeLevel minus one
		maxTreeLevel -- ;
		accountPinPromotionOnNextLevel( maxTreeLevel, lastMonthEndDate );
		*/
		logger.info("accountPinPromotion, finish with date ["+ lastMonthEndDate +"].");
		
	}

	private void storeNewAccountPin(Date lastMonthEndDate, Account account,
			String pinPosition) {
		/*
		AccountPinHistory pinHistory = new AccountPinHistory();
		pinHistory.setAccount(account);
		pinHistory.setPin( account.getPin() );
		pinHistory.setPromotionDate( account.getPromotionDate() );
		logger.debug( "storeNewAccountPin, pinHistory=["+ pinHistory+"]");				
		accountPinHistoryRepository.save( pinHistory );
		
		// set new information
		account.setPin( pinPosition );
		account.setPromotionDate( lastMonthEndDate );				
		logger.debug( "storeNewAccountPin, promote account=["+ account+"]");
		accountService.save( account );
		*/
		throw new IllegalArgumentException("Not yet implemented.");
	}

	private void storeNewAccountPin(Date lastMonthEndDate, Account account,
			CheckPinResult checkPinResult) {
		/*
		// check it is pin promotion or only qualified date change
		if ( ! checkPinResult.getOldPin().equals( checkPinResult.getNewPin() )) {
			// it is pin promotion
			storeNewAccountPin( lastMonthEndDate, account, checkPinResult.getNewPin() );
		}
		
		// store the qualified period
		AccountAward accountAward = new AccountAward();
		accountAward.setAccount(account);
		accountAward.setAwardDate(lastMonthEndDate);
		accountAward.setPin( checkPinResult.getNewPin() );
		
		accountAwardService.save(accountAward);
		*/
		throw new IllegalArgumentException("Not yet implemented.");
	}
	
	
	/*
	 * This function no recursive loop to find out and update Position on next level until it reach the height level = 0 or no childList return
	 */
	private void accountPinPromotionOnNextLevel(int treeLevelNum, Date lastMonthEndDate) {
		if ( treeLevelNum <= 0 ) return;
		logger.info("accountPinPromotionOnNextLevel, start on level ["+ treeLevelNum +"], with date ["+ lastMonthEndDate +"].");
		/*
		while ( treeLevelNum >= 0) {
			logger.debug( "accountPinPromotionOnNextLevel, treeLevelNum=["+ treeLevelNum +"]");

			List<Account> thisTreeLevelAccountList = accountService.findDistributorAccountAtLevel(treeLevelNum);
			// loop for the children to calculate PPV at the lowest level
			for (Account account: thisTreeLevelAccountList) {
				long accountId = account.getId();
				logger.info( "accountPinPromotionOnNextLevel, treeLevelNum=["+ treeLevelNum +"] accountNum=["+account.getAccountNum()+"]");

				
				List<SalesRecord> accountMonthlySales = salesRecordService.retrieveSelfSaleRecordOfLastMonth( accountId, lastMonthEndDate );
				SalesRecord totalSales = salesRecordService.findOutPersonalSalesRecordTotal(accountMonthlySales);
				

				CheckPinResult checkPinResult = checkAccountPinPromotion(account, lastMonthEndDate);
				logger.debug( "accountPinPromotionOnNextLevel, check as at date ["+ lastMonthEndDate+"], accountPin=["+ account.getPin()+"], checkPinResult=["+ checkPinResult+"]");				

				// update Pin to database if pin changed 
				if ( checkPinResult.isPinStatusChanged() ) {
					storeNewAccountPin(lastMonthEndDate, account, checkPinResult );				
				}			
								
			} // end for loop
			
			treeLevelNum -- ;
		}
		*/
		logger.info("accountPinPromotionOnNextLevel, end on level ["+ treeLevelNum +"], with date ["+ lastMonthEndDate +"].");
		
		
		
	}
	
}
