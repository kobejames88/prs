package com.perfectchina.bns.model;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the member database table.
 * 
 */
@Entity
@Table(name = "Member")
@NamedQuery(name="Member.findAll", query="SELECT m FROM Member m")
public class Member implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	private String bankAccountNum;

	private String bankName;

	private String correspondAddr;

	private String countryCode;

	private String createdBy;

	@Temporal(TemporalType.DATE)
	private Date creationDate;

	private String creditCardHolder;

	private String creditCardNum;

	private String deliveryAddr;

	private String discountType;

	private String documentNum;

	private String documentType;

	private String lastUpdatedBy;

	@Temporal(TemporalType.DATE)
	private Date lastUpdatedDate;

	private int mobileNum;

	private String name;

	private String registeredAddr;

	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="AccountId")
	private Account account;

	public Member() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getBankAccountNum() {
		return this.bankAccountNum;
	}

	public void setBankAccountNum(String bankAccountNum) {
		this.bankAccountNum = bankAccountNum;
	}

	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCorrespondAddr() {
		return this.correspondAddr;
	}

	public void setCorrespondAddr(String correspondAddr) {
		this.correspondAddr = correspondAddr;
	}

	public String getCountryCode() {
		return this.countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
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

	public String getCreditCardHolder() {
		return this.creditCardHolder;
	}

	public void setCreditCardHolder(String creditCardHolder) {
		this.creditCardHolder = creditCardHolder;
	}

	public String getCreditCardNum() {
		return this.creditCardNum;
	}

	public void setCreditCardNum(String creditCardNum) {
		this.creditCardNum = creditCardNum;
	}

	public String getDeliveryAddr() {
		return this.deliveryAddr;
	}

	public void setDeliveryAddr(String deliveryAddr) {
		this.deliveryAddr = deliveryAddr;
	}

	public String getDiscountType() {
		return this.discountType;
	}

	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}

	public String getDocumentNum() {
		return this.documentNum;
	}

	public void setDocumentNum(String documentNum) {
		this.documentNum = documentNum;
	}

	public String getDocumentType() {
		return this.documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
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

	public int getMobileNum() {
		return this.mobileNum;
	}

	public void setMobileNum(int mobileNum) {
		this.mobileNum = mobileNum;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegisteredAddr() {
		return this.registeredAddr;
	}

	public void setRegisteredAddr(String registeredAddr) {
		this.registeredAddr = registeredAddr;
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
	
}