package com.perfectchina.bns.service;

import java.util.Date;
import java.util.List;

import com.perfectchina.bns.model.InterfaceAccountInfo;



public interface InterfaceAccountService {

	public final static String ADD = "ADD";
	public final static String MODIFY = "MODIFY";
	public final static String REMOVE = "REMOVE";
	
	public InterfaceAccountInfo findById(Long id);
	
	public void saveInterfaceAccountInfo(InterfaceAccountInfo interfaceAccountInfo);
	
	public void updateInterfaceAccountInfo(InterfaceAccountInfo interfaceAccountInfo);
	
	public void deleteInterfaceAccountInfoById(Long id);
	
	public void deleteAllInterfaceAccountInfos();
	
	public List<InterfaceAccountInfo> storeInterfaceAccountInfo(List<InterfaceAccountInfo> interfaceAccountInfoList);
	
	public List<InterfaceAccountInfo> retrievePendingInterfaceAccountInfo();
	
	// this function confirm imported InterfaceAccountInfo
	public void confirmInterfaceAccountInfo();

	// this function remove those pending InterfaceAccountInfo
	public void removePendingInterfaceAccountInfo();
	
	// This function import new account from InterfaceAccountInfo to SimpleNetTreeNote
	public void convertInterfaceAccountInfoToSimpleNetTreeNode();
	
	public List<InterfaceAccountInfo> enquireInterfaceAccountStatusInfo(Date dateFrom, Date dateTo);
	
	
}
