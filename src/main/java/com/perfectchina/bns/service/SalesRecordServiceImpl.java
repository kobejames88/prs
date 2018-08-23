package com.perfectchina.bns.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.SalesRecord;
import com.perfectchina.bns.repositories.SalesRecordRepository;


@Transactional
@Service
public class SalesRecordServiceImpl implements SalesRecordService {
	
	private final Logger logger = LoggerFactory.getLogger( this.getClass() );

	@Autowired
	private SalesRecordRepository salesRecordRepository;

	public List<SalesRecord> retrieveSelfSaleRecordOfLastMonth(long accountId, Date lastMonthEndDate) {
		logger.debug( "retrieveSelfSaleRecordOfLastMonth, accountId="+accountId+ ", lastMonthEndDate=" + lastMonthEndDate );
		//Date dateFrom = DateUtils.getMonthStartDate(lastMonthEndDate);
		Date dateFrom = DateUtils.getCurrentMonthStartDate(lastMonthEndDate);
		Date dateTo = lastMonthEndDate;
		
		List<SalesRecord> salesRecords = salesRecordRepository.findByAccountId(accountId, dateFrom, dateTo);
		logger.debug( "retrieveSelfSaleRecordOfLastMonth, accountId="+accountId+ ", this salesRecords=" + salesRecords );
				
		return salesRecords;
	}	
	
	/*
	 * It return PV, the result may be neglective for PV adjustment.
	 */
	public SalesRecord findOutPersonalSalesRecordTotal(List<SalesRecord> salesRecordList) {
		SalesRecord total = new SalesRecord();
		float totalPV = 0;		
		float totalRetailSalesAmount = 0;
		float totalAmount = 0;
		for (SalesRecord s: salesRecordList) {
			totalPV += s.getSalesPV();
			totalAmount += s.getSalesAmount();
			totalRetailSalesAmount += s.getRetailSalesAmount();
		}
		total.setRetailSalesAmount(totalRetailSalesAmount);
		total.setSalesAmount(totalAmount);
		total.setSalesPV(totalPV);
		return total;		
	}

	
}
