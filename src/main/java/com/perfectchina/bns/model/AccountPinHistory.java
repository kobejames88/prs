package com.perfectchina.bns.model;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the accountpinhistory database table.
 * 
 */
@Entity
@NamedQuery(name="AccountPinHistory.findAll", query="SELECT a FROM AccountPinHistory a")
public class AccountPinHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	private String createdBy;

	@Temporal(TemporalType.DATE)
	private Date creationDate;

	private String lastUpdatedBy;

	@Temporal(TemporalType.DATE)
	private Date lastUpdatedDate;

	private String pin;

	@Temporal(TemporalType.DATE)
	private Date promotionDate;

	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="AccountId")
	private Account account;

	public AccountPinHistory() {
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

	public String getPin() {
		return this.pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public Date getPromotionDate() {
		return this.promotionDate;
	}

	public void setPromotionDate(Date promotionDate) {
		this.promotionDate = promotionDate;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
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
		return "AccountPinHistory [id=" + id + ", pin=" + pin
				+ ", promotionDate=" + promotionDate + ", account=" + account
				+ "]";
	}

}