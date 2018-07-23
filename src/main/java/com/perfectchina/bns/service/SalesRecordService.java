package com.perfectchina.bns.service;

import java.util.Date;
import java.util.List;

import com.perfectchina.bns.model.SalesRecord;



public interface SalesRecordService {
	public List<SalesRecord> retrieveSelfSaleRecordOfLastMonth(long accountId, Date lastMonthEndDate);
	public SalesRecord findOutPersonalSalesRecordTotal(List<SalesRecord> salesRecordList);
}
