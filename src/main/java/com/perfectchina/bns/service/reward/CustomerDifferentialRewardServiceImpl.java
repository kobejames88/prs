package com.perfectchina.bns.service.reward;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.CustomerBonusRate;
import com.perfectchina.bns.model.CustomerDifferentialBonus;
import com.perfectchina.bns.model.treenode.FiveStarNetTreeNode;
import com.perfectchina.bns.service.CustomerBonusRateService;
import com.perfectchina.bns.service.FiveStarTreeNodeService;


@Transactional
@Service
public class CustomerDifferentialRewardServiceImpl implements CustomerDifferentialRewardService {

	private final Logger logger = LoggerFactory.getLogger( this.getClass() );
	
	@Autowired
	private FiveStarTreeNodeService fiveStarTreeNodeService;
	
	@Autowired
	private CustomerBonusRateService customerBonusRateService;

	

	
	
	// this function loop the list to get the current month account reward
	private CustomerDifferentialBonus getCurrentMonthAccountReward(List<CustomerDifferentialBonus> accountRewards, Date lastMonthEndDate) {
		CustomerDifferentialBonus currentMonthAccountReward = null;
		if ( accountRewards.size() > 0 ) {
			CustomerDifferentialBonus  tempReward = accountRewards.get( accountRewards.size() - 1 );
			
			// check if the last one is equal to checkAsAtDate
			Date lastMonthStartDate = DateUtils.getMonthStartDate(lastMonthEndDate);
			
			if ( lastMonthStartDate.after( tempReward.getRewardDate() ) ) {
				// there are no current month account Reward ? Error
				throw new IllegalArgumentException("There are no current month ["+ lastMonthEndDate +"] account reward, please run awardService first.");
			} else {
				currentMonthAccountReward = tempReward;
			}
		}
		return currentMonthAccountReward;
	}
	
	// this function only set qualification level and bonus rate
	private CustomerDifferentialBonus calculateAccountRewardBonusRateAndAmount(CustomerDifferentialBonus currentMonthReward, CustomerDifferentialBonus previousMonthReward, Date lastMonthEndDate) {
			
		// compare previousMonthReward.bonusRate to the calculated bonus rate to get the highest rate
		
		CustomerBonusRate bonusRate = customerBonusRateService.getBonusRateByAopvDesc(currentMonthReward.getAopv(), lastMonthEndDate) ;
		logger.debug( "calculateAccountRewardBonusRate, currentMonthReward=[" + currentMonthReward+
				", bonusRate=["+bonusRate+"], previousMonthReward=[" + previousMonthReward +"]");
		
		if ( bonusRate == null ) {
			// it should set the bonus Rate first
			throw new IllegalArgumentException("BonusRate for date ["+ lastMonthEndDate +"] not setup");
		}
		
		// get the bonus rate on different level
		
		// calcuate the bonus amount for different level 
		//float pbv = currentMonthReward.getPbv() ;
		//float rewardAmount = pbv * currentMonthReward.getBonusRate() ;
		
		// currentMonthReward.setAmount( rewardAmount );
		
		logger.debug( "calculateAccountRewardBonusRate, result currentMonthReward=[" + currentMonthReward+"]" );
		return currentMonthReward;
	}	
	
	// This function update the currentMonthReward HasBonus flag and reset the bonus rate 
	private CustomerDifferentialBonus determineAccountRewardHasBonus(FiveStarNetTreeNode node, Date lastMonthEndDate) {

		CustomerDifferentialBonus currentMonthAccountReward = null;
		
		// if account entitle to has bonus ( such as active member )
		//currentMonthAccountReward.setHasBonus(true);				
		//currentMonthAccountReward = calculateAccountRewardBonusRateAndAmount(currentMonthAccountReward, previousMonthAccountReward, lastMonthEndDate );
		
		// else if account do not entitle to has bonus ( such as inactive member )
		// currentMonthAccountReward.setHasBonus(false);
		// currentMonthAccountReward.setBonusRate( 0 );
		// currentMonthAccountReward.setAmount( 0 );
		
				
		//logger.debug( "determineAccountRewardHasBonus, return currentMonthAccountReward=[" + currentMonthAccountReward +"]" );
		return currentMonthAccountReward;
	}
	
	// This function find the bonus rate then calculate the reward
	// Then it will add the information to AccountReward
	@Override
	public void calculateReward(Date lastMonthEndDate) {
		
		String snapShotDate = DateUtils.convertToSnapShotDate(lastMonthEndDate);
		List<FiveStarNetTreeNode> childList = fiveStarTreeNodeService.findChildLeafList( snapShotDate );
		logger.info( "calculateReward, start with date=["+lastMonthEndDate+"]");
		
		int maxTreeLevel = 0;
		// get max tree level from DB		
		maxTreeLevel = fiveStarTreeNodeService.getMaxTreeLevel( snapShotDate );
		
		rewardFindingOnNextLevel( maxTreeLevel, lastMonthEndDate ); // this method calculate reward from bottom to root
		logger.info( "calculateCashReward, end with date=["+lastMonthEndDate+"]");
		
	}

	
	private void rewardFindingOnNextLevel(int treeLevelNum, Date lastMonthEndDate) {
		if ( treeLevelNum <= 0 ) return;
		logger.info( "rewardFindingOnNextLevel, start levelNum=["+treeLevelNum+"], with date=["+lastMonthEndDate+"]");
		String snapShotDate = DateUtils.convertToSnapShotDate(lastMonthEndDate);
		while ( treeLevelNum > 0) {
			logger.debug( "rewardFindingOnNextLevel, treeLevelNum=["+ treeLevelNum +"]");

			List<FiveStarNetTreeNode> thisTreeLevelNodeList = fiveStarTreeNodeService.findNodeAtLevel( snapShotDate, treeLevelNum );
			// loop for the children to calculate PPV at the lowest level
			for (FiveStarNetTreeNode node: thisTreeLevelNodeList) {
				//long accountId = account.getId();
				//logger.info( "rewardFindingOnNextLevel, treeLevelNum=["+ treeLevelNum +"] accountId=["+accountId+"]");

				CustomerDifferentialBonus currentMonthAccountReward = determineAccountRewardHasBonus(node, lastMonthEndDate);
				// updateCustomerDifferentialBonus(currentMonthAccountReward);
				logger.debug( "rewardFindingOnNextLevel, updated currentMonthAccountReward=[" + currentMonthAccountReward +"]" );
				
			} // end for loop
			
			treeLevelNum -- ;
		}
		logger.info( "rewardFindingOnNextLevel, finish levelNum=["+treeLevelNum+"], with date=["+lastMonthEndDate+"]");
	}
	
}
