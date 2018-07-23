package com.perfectchina.bns.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the axsalesrecordinfo database table.
 * 
 */
@Entity
@NamedQuery(name="InterfaceSalesRecordInfo.findAll", query="SELECT t FROM InterfaceSalesRecordInfo t")
public class InterfaceSalesRecordInfo implements Serializable, Comparable<InterfaceSalesRecordInfo> {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	private String accountNum;

	@Temporal(TemporalType.DATE)
	private Date salesDate;

	private String orderNum;

	private float salesPv;

	@Temporal(TemporalType.DATE)
	private Date requestDate;

	private String requestStatus;
		
	public InterfaceSalesRecordInfo() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	
	public Date getSalesDate() {
		return this.salesDate;
	}

	public void setSalesDate(Date salesDate) {
		this.salesDate = salesDate;
	}

	public String getAccountNum() {
		return accountNum;
	}

	public void setAccountNum(String accountNum) {
		this.accountNum = accountNum;
	}

	public float getSalesPv() {
		return salesPv;
	}

	public void setSalesPv(float salesPv) {
		this.salesPv = salesPv;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	
	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}


    @Override
	public String toString() {
		return "InterfaceSalesRecordInfo [id=" + id + ", accountNum=" + accountNum
				+ ", salesDate=" + salesDate + ", orderNum=" + orderNum
				+ ", salesPv=" + salesPv
				+ ", requestDate=" + requestDate + ", requestStatus="
				+ requestStatus + "]";
	}

	public int compareTo(InterfaceSalesRecordInfo o) {
        return Comparators.ORDERNUM.compare(this, o);
    }
    
	public static class Comparators {

        public static Comparator<InterfaceSalesRecordInfo> ORDERNUM = new Comparator<InterfaceSalesRecordInfo>() {
            @Override
            public int compare(InterfaceSalesRecordInfo o1, InterfaceSalesRecordInfo o2) {
            	if ( o1.orderNum == null ) {
            		return -1;
            	} else {
            		return o1.orderNum.compareTo(o2.orderNum);
            	}
            }
        };
    }	


}