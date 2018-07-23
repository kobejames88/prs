package com.perfectchina.bns.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.perfectchina.bns.model.InterfaceSalesRecordInfo;


public interface InterfaceSalesRecordService {

	public List<InterfaceSalesRecordInfo> retrievePendingInterfaceSalesRecordInfo();
	
	// Remove the newly imported Interface SalesRecord
	public void removePendingInterfaceSalesRecordInfo();

	// This method confirm the Pending Interface SalesRecord
	public void confirmInterfaceSalesRecordInfo();
	
	// This function convert new account with confirm status from InterfaceAccountInfo to Account
	public void convertInterfaceSalesRecordInfoToSalesRecord();
	
	public List<InterfaceSalesRecordInfo> enquireInterfaceSalesRecordInfo(Date dateFrom, Date dateTo);

	public void uploadSalesRecords(List<InterfaceSalesRecordInfo> list);
	
}
