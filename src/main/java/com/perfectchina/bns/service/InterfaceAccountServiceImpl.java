package com.perfectchina.bns.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.perfectchina.bns.common.utils.SortingUtils;
import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.AccountStatus;
import com.perfectchina.bns.model.InterfaceAccountInfo;
import com.perfectchina.bns.model.InterfaceInfoStatus;
import com.perfectchina.bns.model.treenode.SimpleNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.AccountRepository;
import com.perfectchina.bns.repositories.InterfaceAccountInfoRepository;
import com.perfectchina.bns.repositories.TreeNodeRepository;

@Service
public class InterfaceAccountServiceImpl implements InterfaceAccountService {

    private static final Logger logger = LoggerFactory.getLogger(InterfaceAccountServiceImpl.class);
    
	@Autowired
	@Qualifier("simpleTreeNodeServiceImpl")
	private TreeNodeService treeNodeService;
    
	@Autowired
	private TreeNodeRepository<SimpleNetTreeNode> treeNodeRepository;
	
	@Autowired
	private InterfaceAccountInfoRepository interfaceAccountInfoRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	public InterfaceAccountInfo findById(Long id) {
		return interfaceAccountInfoRepository.findOne(id);
	}
	
	public List<InterfaceAccountInfo> storeInterfaceAccountInfo(List<InterfaceAccountInfo> interfaceAccountInfoList) {
		
		// Check the status of the account
		for (InterfaceAccountInfo axAccountInfo: interfaceAccountInfoList ) {
			axAccountInfo.setRequestStatus( InterfaceInfoStatus.PENDING );			
		}
		
		Collections.sort(interfaceAccountInfoList, InterfaceAccountInfo.Comparators.ACCOUNTNUM );
		
		// save model class data in DB
		List<InterfaceAccountInfo> resultList = interfaceAccountInfoRepository.save( interfaceAccountInfoList);
		
		return resultList;		
		
	}

	public void saveInterfaceAccountInfo(InterfaceAccountInfo interfaceAccountInfo) {
		logger.debug( "saveInterfaceAccountInfo, " + interfaceAccountInfo);
		interfaceAccountInfoRepository.save( interfaceAccountInfo);
	}

	public void updateInterfaceAccountInfo(InterfaceAccountInfo interfaceAccountInfo) {
		logger.debug( "updateInterfaceAccountInfo, " + interfaceAccountInfo);
		interfaceAccountInfoRepository.save( interfaceAccountInfo);
	}

	public void deleteInterfaceAccountInfoById(Long id) {
		logger.debug( "deleteInterfaceAccountInfo, " + id);
		if ( id != null ) {
			interfaceAccountInfoRepository.delete(id);
		}
	}
	
	public void deleteAllInterfaceAccountInfos() {
		interfaceAccountInfoRepository.deleteAll();
	}
	
	@Override
	public void convertInterfaceAccountInfoToSimpleNetTreeNode() {
		logger.debug("convertInterfaceAccountInfoToSimpleNetTreeNode, start");
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		String snapshotDate = format.format(new Date());
		
		// only retrieve the requestStatus corresponding to operationDate
		List<InterfaceAccountInfo> interfaceAccountInfoList = interfaceAccountInfoRepository.findByRequestStatus(InterfaceInfoStatus.CONFIRMED );
		
		// for each InterfaceAccountInfo, need to check if the action is new, edit or delete
		for (InterfaceAccountInfo interfaceAccount: interfaceAccountInfoList ){
			SimpleNetTreeNode temp = treeNodeRepository.getAccountByAccountNum( interfaceAccount.getAccountNum() );
			// check the action 
			logger.debug("convertInterfaceAccountInfoToSimpleNetTreeNode, interfaceAccount=["+interfaceAccount+"]");
			
			if ( ( InterfaceAccountService.ADD.equals( interfaceAccount.getAction() ) || ( InterfaceAccountService.MODIFY.equals( interfaceAccount.getAction() ) ) ) ) {
				// add a new one for non-existence record
				if ( temp == null ) { // No existence Account record, add one
					String uplinkId = interfaceAccount.getUplinkAccount();
					if ( ( uplinkId == null ) || ( "".equals(uplinkId)) ) {
						SimpleNetTreeNode newAccount = createNewSimpleNetTreeNode( interfaceAccount, null );
						//RootTreeNode
						newAccount.setLevelNum(0);
						newAccount.setSnapshotDate(snapshotDate);
						treeNodeRepository.saveAndFlush(newAccount);
						// update the InterfaceAccountInfo request status to imported
						interfaceAccount.setRequestStatus( InterfaceInfoStatus.IMPORTED );
					} else { // there are uplink
						TreeNode uplink = treeNodeRepository.getAccountByAccountNum( interfaceAccount.getUplinkAccount() );
						if ( uplink == null ) {
							// not a valid interfaceAccount information
							logger.warn("convertInterfaceAccountInfoToAccount, skip invalid uplink Account for interfaceAccount ["+interfaceAccount+"]." );					
							interfaceAccount.setRequestStatus( InterfaceInfoStatus.SKIPPED );
						} else {
							SimpleNetTreeNode newAccount = createNewSimpleNetTreeNode( interfaceAccount, String.valueOf( uplink.getId() ) );
							newAccount.setSnapshotDate(snapshotDate);
							treeNodeRepository.saveAndFlush(newAccount);
							// update the InterfaceAccountInfo request status to imported
							interfaceAccount.setRequestStatus( InterfaceInfoStatus.IMPORTED );
						}
					}
				} else { // there are existence account in GAR, modify it
					SimpleNetTreeNode oldAccount = null;
					String axAccountUplinkId = interfaceAccount.getUplinkAccount();
					if ( ( axAccountUplinkId == null ) || ( "".equals(axAccountUplinkId)) ) {
						// uplink should not be null
						logger.warn("convertInterfaceAccountInfoToAccount, skip empty uplink Account for interfaceAccount ["+interfaceAccount+"]." );					
						interfaceAccount.setRequestStatus( InterfaceInfoStatus.SKIPPED );
					} else {
						// check if it is valid uplinkId
						SimpleNetTreeNode axAccountUplink = treeNodeRepository.getAccountByAccountNum( interfaceAccount.getUplinkAccount() );
						if ( axAccountUplink == null ) {
							// not a valid interfaceAccount information
							logger.warn("convertInterfaceAccountInfoToAccount, skip invalid uplink Account for interfaceAccount ["+interfaceAccount+"]." );					
							interfaceAccount.setRequestStatus( InterfaceInfoStatus.SKIPPED );
						} else {
							if ( axAccountUplink.getId() != temp.getUplinkId() ) {
								// the uplink is changed, need to change the account uplinkId and all its child tree level number
								oldAccount = modifyAccountAndTreeLevel( interfaceAccount, axAccountUplink, temp );
								oldAccount.setSnapshotDate(snapshotDate);
								treeNodeRepository.saveAndFlush(oldAccount);			
								interfaceAccount.setRequestStatus( InterfaceInfoStatus.IMPORTED );
								
							} else {
								// uplink not changed
								// modify the Account name and status
								oldAccount = modifyAccount( interfaceAccount, temp );
								oldAccount.setSnapshotDate(snapshotDate);
								treeNodeRepository.saveAndFlush(oldAccount);			
								interfaceAccount.setRequestStatus( InterfaceInfoStatus.IMPORTED );
								
							}
							
							
						}						
					}
					
				}
				
			} else if ( InterfaceAccountService.REMOVE.equals( interfaceAccount.getAction() ) ) {
				if ( temp == null ) {
					logger.warn("convertInterfaceAccountInfoToAccount, skip non-exist Account for interfaceAccount ["+interfaceAccount+"]." );					
					interfaceAccount.setRequestStatus( InterfaceInfoStatus.SKIPPED );
				} else {
					// only remove account without child
					List<SimpleNetTreeNode> childList = treeNodeRepository.findByParentId( temp.getId() );
					if ( childList.size() == 0 ) {
						// can remove it
						SimpleNetTreeNode oldAccount = markDeleteAccount( interfaceAccount, temp );
						oldAccount.setSnapshotDate(snapshotDate);
						treeNodeRepository.saveAndFlush(oldAccount);					
						interfaceAccount.setRequestStatus( InterfaceInfoStatus.IMPORTED );
						
					} else {
						logger.warn("convertInterfaceAccountInfoToAccount, skip remove Account as it has downline for interfaceAccount ["+interfaceAccount+"]." );					
						interfaceAccount.setRequestStatus( InterfaceInfoStatus.SKIPPED );
					}
					
				}
				
			} else {
				logger.warn("convertInterfaceAccountInfoToAccount, skip invalid interfaceAccount information ["+interfaceAccount+"]." );
				interfaceAccount.setRequestStatus( InterfaceInfoStatus.SKIPPED );
			}
			
		} // end for loop
		
		// need to update HasChild Field in AccountInfo
		updateHasChildField();
		
		// finally, need to update back the interfaceAccount
		interfaceAccountInfoRepository.save( interfaceAccountInfoList );
		
		logger.debug("convertInterfaceAccountInfoToAccount, end");
		
	}
	
	
	private SimpleNetTreeNode createNewSimpleNetTreeNode(InterfaceAccountInfo interfaceAccount, String uplinkId ) {
		
		Account account = new Account();
		account.setAccountNum( interfaceAccount.getAccountNum() );
		account.setExpiryDate( interfaceAccount.getExpiryDate() );
		account.setJoinDate( interfaceAccount.getJoinDate() );
		account.setPromotionDate( interfaceAccount.getJoinDate() ); // use join date as the first promotion date
		account.setAddress( interfaceAccount.getAccountAddress() );
		// account.setHasChild( interfaceAccount.getHasChild() );
		account.setName( interfaceAccount.getAccountName() );
		account.setPin( interfaceAccount.getPin() );
		// account.setPromotionDate( interfaceAccount.getPromotionDate() );

		account.setStatus( interfaceAccount.getStatus() );
		accountRepository.save(account);
		logger.debug("createNewAccount, account="+account );
		
		SimpleNetTreeNode newNode = new SimpleNetTreeNode();
		newNode.setData(account);
		if ( uplinkId != null ) {
			long uplink = Long.parseLong( uplinkId );
			newNode.setUplinkId(uplink);
		}
		newNode.setHasChild( false ); // since interfaceAccount no such information, we preset it to false and later will update it 
		newNode.setLevelNum( null ); // since interfaceAccount no such information, we preset it to null and later will update it 
		
		
		return newNode;
	}

	private SimpleNetTreeNode modifyAccount(InterfaceAccountInfo interfaceAccount, SimpleNetTreeNode thisNode ) {
		
		Account account = thisNode.getData();
		account.setExpiryDate( interfaceAccount.getExpiryDate() );
		account.setName( interfaceAccount.getAccountName() );
		// After the call from Hans on 201507 first week, VIP will not promote to T, 
		// they will use another account number for T
		// from the email of 20160204 from Felix, it confirmed that AX will update the Pin and feedback to GARS
		//account.setPin( interfaceAccount.getPin() ); // If it promote from VIP to T on AX System
		
		// need to check if the account has changed uplinkId
		
		account.setStatus( interfaceAccount.getStatus() );
		account.setAddress( interfaceAccount.getAccountAddress() );
		
		return thisNode;
	}

	/**
	 * This function modify the account and all its child tree level 
	 * 
	 * @param interfaceAccount the verified axAccoun with correct uplinkId
	 * @param uplinkAccount
	 * @param account
	 * @return
	 */
	private SimpleNetTreeNode modifyAccountAndTreeLevel(InterfaceAccountInfo interfaceAccount, SimpleNetTreeNode parentNode, SimpleNetTreeNode thisNode ) {
		
		Account account = thisNode.getData();
		account.setExpiryDate( interfaceAccount.getExpiryDate() );
		account.setName( interfaceAccount.getAccountName() );
		
		// from the email of 20160204 from Felix, it confirmed that AX will update the Pin and feedback to GARS
		// account.setPin( interfaceAccount.getPin() ); // If it promote from VIP to T on AX System
		
		thisNode.setUplinkId( parentNode.getId() );
		
		int levelNum = parentNode.getLevelNum() + 1;
		thisNode.setLevelNum( levelNum );
		
		
		treeNodeService.updateChildTreeLevel(levelNum, thisNode );

		
		account.setStatus( interfaceAccount.getStatus() );
		account.setAddress( interfaceAccount.getAccountAddress() );
		
		return thisNode;
	}
		
	
	private SimpleNetTreeNode markDeleteAccount(InterfaceAccountInfo interfaceAccount, SimpleNetTreeNode thisNode ) {
		
		Account account = thisNode.getData();
		account.setStatus( AccountStatus.MARK_DELETED );		
		return thisNode;
	}

	@Override
	public List<InterfaceAccountInfo> retrievePendingInterfaceAccountInfo() {
		logger.debug("retrievePendingInterfaceAccountInfo, start");
		List<InterfaceAccountInfo> interfaceAccountInfoList = interfaceAccountInfoRepository.findByRequestStatus(InterfaceInfoStatus.PENDING);
		logger.debug("retrievePendingInterfaceAccountInfo, end");
		return interfaceAccountInfoList;
	}
	

	@Override
	public void confirmInterfaceAccountInfo() {
		logger.debug("confirmInterfaceAccountInfo, start");
		List<InterfaceAccountInfo> interfaceAccountInfoList = interfaceAccountInfoRepository.findByRequestStatus(InterfaceInfoStatus.PENDING);
		for (InterfaceAccountInfo interfaceAccount: interfaceAccountInfoList ){
			interfaceAccount.setRequestStatus( InterfaceInfoStatus.CONFIRMED );
		}		
		interfaceAccountInfoRepository.save( interfaceAccountInfoList );
		logger.debug("confirmInterfaceAccountInfo, end");
		
	}
	

	@Override
	public void removePendingInterfaceAccountInfo() {
		logger.debug("removePendingInterfaceAccountInfo, start");
		List<InterfaceAccountInfo> interfaceAccountInfoList = interfaceAccountInfoRepository.findByRequestStatus(InterfaceInfoStatus.PENDING);
		interfaceAccountInfoRepository.delete(interfaceAccountInfoList);
		logger.debug("removePendingInterfaceAccountInfo, end");
	}
	
	private void updateHasChildField() {
		logger.debug("updateHasChildField, start");
		List<SimpleNetTreeNode> hasChildAccounts = treeNodeRepository.retrieveInCorrectHasChildAccounts();
		// update the fields
		for ( TreeNode account : hasChildAccounts ) {
			account.setHasChild( true );
		}
		treeNodeRepository.save( hasChildAccounts );
		
		List<SimpleNetTreeNode> leafAccounts = treeNodeRepository.retrieveInCorrectLeafAccounts();
		// update the fields
		for ( SimpleNetTreeNode account : leafAccounts ) {
			account.setHasChild( false );
		}
		treeNodeRepository.save( leafAccounts );
		
		
		logger.debug("updateHasChildField, end");
		
	}
	
	@Override
	public List<InterfaceAccountInfo> enquireInterfaceAccountStatusInfo(Date dateFrom, Date dateTo) {
		logger.debug("enquireAXAccountStatusInfo, start");
		List<InterfaceAccountInfo> dbList = interfaceAccountInfoRepository.findByJoinDate(dateFrom, dateTo);

		// put it in hasMap to make account Num unique
		HashMap<String, InterfaceAccountInfo> uniqueMap = new HashMap<String, InterfaceAccountInfo>();
		for ( InterfaceAccountInfo temp: dbList ) {
			uniqueMap.put( temp.getAccountNum(), temp );
		}
		
		SortedSet<InterfaceAccountInfo> sortedSet = new TreeSet<InterfaceAccountInfo>();
		sortedSet.addAll( uniqueMap.values() );
		
		Collection<InterfaceAccountInfo> unsorted = uniqueMap.values();
		List<InterfaceAccountInfo> interfaceAccountInfoList = SortingUtils.asSortedList(unsorted);
		
		logger.debug("enquireAXAccountStatusInfo, end");
		return interfaceAccountInfoList;
	}


}
