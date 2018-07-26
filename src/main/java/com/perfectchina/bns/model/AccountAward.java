package com.perfectchina.bns.model;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the accountaward database table.
 * 
 */
@Entity
@NamedQuery(name="AccountAward.findAll", query="SELECT a FROM AccountAward a")
public class AccountAward implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)	
	private long id;

	private String pin;
	
	private Date awardDate;
	
	private String createdBy;

	@Temporal(TemporalType.DATE)
	private Date creationDate;

	private String lastUpdatedBy;

	@Temporal(TemporalType.DATE)
	private Date lastUpdatedDate;


	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="AccountId")
	private Account account;

	public AccountAward() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}



	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}


	public String getLastUpdatedBy() {
		return this.lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public Date getLastUpdatedDate() {
		return this.lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Date getAwardDate() {
		return awardDate;
	}

	public void setAwardDate(Date awardDate) {
		this.awardDate = awardDate;
	}

	@PreUpdate
	@PrePersist
	public void updateTimeStamps() {
		Date today = new Date();
	    lastUpdatedDate = today;
	    if (creationDate==null) {
	    	creationDate = today;
	    }
	}

	@Override
	public String toString() {
		return "AccountAward [id=" + id + ", pin=" + pin 
				+ ", awardDate=" + awardDate 
				+ ", createdBy=" + createdBy + ", creationDate=" + creationDate
				+ ", lastUpdatedBy=" + lastUpdatedBy + ", lastUpdatedDate="
				+ lastUpdatedDate + ", account=" + account + "]";
	}
	




}