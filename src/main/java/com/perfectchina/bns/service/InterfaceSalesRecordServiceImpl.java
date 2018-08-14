package com.perfectchina.bns.service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perfectchina.bns.common.utils.SortingUtils;
import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.InterfaceInfoStatus;
import com.perfectchina.bns.model.InterfaceSalesRecordInfo;
import com.perfectchina.bns.model.SalesRecord;
import com.perfectchina.bns.repositories.AccountRepository;
import com.perfectchina.bns.repositories.InterfaceSalesRecordInfoRepository;
import com.perfectchina.bns.repositories.SalesRecordRepository;

@Service
public class InterfaceSalesRecordServiceImpl implements InterfaceSalesRecordService {

	private final Logger logger = LoggerFactory.getLogger( this.getClass() );

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private SalesRecordRepository salesRecordRepository;

	@Autowired
	private InterfaceSalesRecordInfoRepository interfaceSalesRecordInfoRepository;
	
	@Override
	public void uploadSalesRecords(List<InterfaceSalesRecordInfo> list) {
		logger.debug("uploadPendingInterfaceSalesRecordInfo, start");
		interfaceSalesRecordInfoRepository.saveAll(list);
		logger.debug("uploadPendingInterfaceSalesRecordInfo, end");
	}
	
	@Override
	public void convertInterfaceSalesRecordInfoToSalesRecord() {
		// only retrieve the requestDate corresponding to operationDate
		List<InterfaceSalesRecordInfo> interfaceSalesRecordInfoList = interfaceSalesRecordInfoRepository.findByRequestStatus(InterfaceInfoStatus.CONFIRMED );
		
		for ( InterfaceSalesRecordInfo axSale : interfaceSalesRecordInfoList ) {
			Account account = accountRepository.getAccountByAccountNum( axSale.getAccountNum() );
			// check the action 
			logger.debug("importInterfaceSalesRecordInfoToSalesRecord, axSale=["+axSale+"]");
			if ( account == null ) {
				logger.warn("importInterfaceSalesRecordInfoToSalesRecord, skip non-exist AccountNum for axSale ["+axSale+"]." );
				axSale.setRequestStatus( InterfaceInfoStatus.SKIPPED );
				
			} else {
				SalesRecord oldSalesRecord = salesRecordRepository.getByOrderNum( axSale.getOrderNum(), axSale.getSalesDate() );
				if ( oldSalesRecord != null ) { // already has old record
					// modify old record
					oldSalesRecord.setSalesPV( axSale.getSalesPv() );
					oldSalesRecord.setAccount(account);
					salesRecordRepository.saveAndFlush(oldSalesRecord);
					
				} else {
					SalesRecord newSalesRecord = createNewSalesRecord( axSale, account );
					salesRecordRepository.saveAndFlush(newSalesRecord);
				}
				axSale.setRequestStatus( InterfaceInfoStatus.IMPORTED );
			}
		}
		// store the status back to DB
		interfaceSalesRecordInfoRepository.saveAll(interfaceSalesRecordInfoList);
	}

	private SalesRecord createNewSalesRecord(InterfaceSalesRecordInfo axSale, Account account ) {
		SalesRecord salesRecord = new SalesRecord();
		salesRecord.setOrderNum( axSale.getOrderNum() );
		salesRecord.setSalesDate( axSale.getSalesDate() );
		salesRecord.setSalesPV( axSale.getSalesPv() );
		salesRecord.setAccount(account);
		
		return salesRecord;
	}	
	
	@Override
	public List<InterfaceSalesRecordInfo> retrievePendingInterfaceSalesRecordInfo() {
		logger.debug("retrievePendingInterfaceSalesRecordInfo, start");
		List<InterfaceSalesRecordInfo> interfaceSalesRecordInfoList = interfaceSalesRecordInfoRepository.findByRequestStatus(InterfaceInfoStatus.PENDING);

		logger.debug("retrievePendingInterfaceSalesRecordInfo, end");
		return interfaceSalesRecordInfoList ;
	}
	
	@Override
	public void confirmInterfaceSalesRecordInfo() {
		logger.debug("confirmInterfaceSalesRecordInfo, start");
		List<InterfaceSalesRecordInfo> interfaceSalesRecordInfoList = interfaceSalesRecordInfoRepository.findByRequestStatus(InterfaceInfoStatus.PENDING);
		for (InterfaceSalesRecordInfo interfaceSalesRecord: interfaceSalesRecordInfoList ){
			interfaceSalesRecord.setRequestStatus( InterfaceInfoStatus.CONFIRMED );
		}		
		interfaceSalesRecordInfoRepository.saveAll( interfaceSalesRecordInfoList );
		logger.debug("confirmInterfaceSalesRecordInfo, end");
		
	}


	@Override
	public void removePendingInterfaceSalesRecordInfo() {
		logger.debug("removePendingInterfaceSalesRecordInfo, start");
		List<InterfaceSalesRecordInfo> interfaceSalesRecordInfoList = interfaceSalesRecordInfoRepository.findByRequestStatus(InterfaceInfoStatus.PENDING);
		interfaceSalesRecordInfoRepository.deleteAll(interfaceSalesRecordInfoList);
		logger.debug("removePendingInterfaceSalesRecordInfo, end");
	}
	
	@Override
	public List<InterfaceSalesRecordInfo> enquireInterfaceSalesRecordInfo(Date dateFrom, Date dateTo) {
		logger.debug("enquireInterfaceSalesRecordInfo, start");
		List<InterfaceSalesRecordInfo> dbList = interfaceSalesRecordInfoRepository.findBySalesDate(dateFrom, dateTo);
		
		// put it in hasMap to make account Num unique
		HashMap<String, InterfaceSalesRecordInfo> uniqueMap = new HashMap<String, InterfaceSalesRecordInfo>();
		for ( InterfaceSalesRecordInfo temp: dbList ) {
			uniqueMap.put( temp.getOrderNum(), temp );
		}
		
		SortedSet<InterfaceSalesRecordInfo> sortedSet = new TreeSet<InterfaceSalesRecordInfo>();
		sortedSet.addAll( uniqueMap.values() );
		
		Collection<InterfaceSalesRecordInfo> unsorted = uniqueMap.values();
		List<InterfaceSalesRecordInfo> interfaceSalesRecordInfoList = SortingUtils.asSortedList(unsorted);
		
		logger.debug("enquireInterfaceSalesRecordInfo, end");
		return interfaceSalesRecordInfoList;
	}

	
}
