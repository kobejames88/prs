package com.perfectchina.bns.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the axaccountinfo database table.
 * 
 */
@Entity
@Table(name = "InterfaceAccountInfo")
@NamedQuery(name="InterfaceAccountInfo.findAll", query="SELECT t FROM InterfaceAccountInfo t")
public class InterfaceAccountInfo implements Serializable, Comparable<InterfaceAccountInfo> {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private String accountNum;

	private String accountName;

	private String accountAddress;
	
	private String uplinkAccount;

	private String pin;
	
	private String status;

	@Temporal(TemporalType.DATE)
	private Date joinDate;

	@Temporal(TemporalType.DATE)
	private Date expiryDate;

	private String action;

	@Temporal(TemporalType.DATE)
	private Date actionDate;
	
	@Temporal(TemporalType.DATE)
	private Date requestDate;

	private String requestStatus;
	
	
	public InterfaceAccountInfo() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getAccountName() {
		return this.accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	
	public String getAccountAddress() {
		return accountAddress;
	}

	public void setAccountAddress(String accountAddress) {
		this.accountAddress = accountAddress;
	}

	public Date getExpiryDate() {
		return this.expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Date getJoinDate() {
		return this.joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAccountNum() {
		return accountNum;
	}

	public void setAccountNum(String accountNum) {
		this.accountNum = accountNum;
	}

	public String getUplinkAccount() {
		return uplinkAccount;
	}

	public void setUplinkAccount(String uplinkAccount) {
		this.uplinkAccount = uplinkAccount;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
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
		return "AXAccountInfo [id=" + id + ", accountNum=" + accountNum
				+ ", accountName=" + accountName + ", accountAddress="
				+ accountAddress + ", uplinkAccount=" + uplinkAccount
				+ ", pin=" + pin
				+ ", status=" + status + ", joinDate=" + joinDate
				+ ", expiryDate=" + expiryDate 
				+ ", action=" + action + ", actionDate=" + actionDate
				+ ", requestDate=" + requestDate + ", requestStatus="
				+ requestStatus + "]";
	}
	

    @Override
    public int compareTo(InterfaceAccountInfo o) {
        return Comparators.ACCOUNTNUM.compare(this, o);
    }
    
	public static class Comparators {

        public static Comparator<InterfaceAccountInfo> ACCOUNTNUM = new Comparator<InterfaceAccountInfo>() {
            @Override
            public int compare(InterfaceAccountInfo o1, InterfaceAccountInfo o2) {
                return o1.accountNum.compareTo(o2.accountNum);
            }
        };
    }	
}