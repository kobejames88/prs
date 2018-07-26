package com.perfectchina.bns.jsonDto;

//[{"accountNum":"A","salesDate":"2018-01-01","orderNum":"","salesPV":3000},{"accountNum":"B","salesDate":"2018-01-01","orderNum":"","salesPV":6000},{"accountNum":"C","salesDate":"2018-01-01","orderNum":"","salesPV":300},{"accountNum":"D","salesDate":"2018-01-01","orderNum":"","salesPV":12000},{"accountNum":"E","salesDate":"2018-01-01","orderNum":"","salesPV":6000},{"accountNum":"F","salesDate":"2018-01-01","orderNum":"","salesPV":400},{"accountNum":"G","salesDate":"2018-01-01","orderNum":"","salesPV":1000}]
public class SalesRecordInfoJson {
	
	String accountNum;
	String salesDate;
	String orderNum;
	String salesPV;
	
	public String getAccountNum() {
		return accountNum;
	}
	public void setAccountNum(String accountNum) {
		this.accountNum = accountNum;
	}
	public String getSalesDate() {
		return salesDate;
	}
	public void setSalesDate(String salesDate) {
		this.salesDate = salesDate;
	}
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	public String getSalesPV() {
		return salesPV;
	}
	public void setSalesPV(String salesPV) {
		this.salesPV = salesPV;
	}
	
}
