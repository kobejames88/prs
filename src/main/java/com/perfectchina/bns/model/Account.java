package com.perfectchina.bns.model;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the account database table.
 * 
 */
@Entity
@Table(name = "Account")
@NamedQuery(name="Account.findAll", query="SELECT a FROM Account a")
public class Account implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	private String createdBy;

	@Temporal(TemporalType.DATE)
	private Date creationDate;

	@Temporal(TemporalType.DATE)
	private Date expiryDate;

	@Temporal(TemporalType.DATE)
	private Date joinDate;

	private String lastUpdatedBy;

	@Temporal(TemporalType.DATE)
	private Date lastUpdatedDate;

	private String name;
	
	private String address;

	private String accountNum;	
	
	private String pin;

	@Temporal(TemporalType.DATE)
	private Date promotionDate;

	private String status;

	//bi-directional many-to-one association to Member
	@OneToMany(mappedBy="account")
	private List<Member> members;

	//bi-directional many-to-one association to Salesrecord
	@OneToMany(mappedBy="account")
	@JsonIgnore
	private List<SalesRecord> salesrecords;


	public Account() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccountNum() {
		return accountNum;
	}

	public void setAccountNum(String accountNum) {
		this.accountNum = accountNum;
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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
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
	

	public List<Member> getMembers() {
		return this.members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}

	public Member addMember(Member member) {
		getMembers().add(member);
		member.setAccount(this);

		return member;
	}

	public Member removeMember(Member member) {
		getMembers().remove(member);
		member.setAccount(null);

		return member;
	}

	public List<SalesRecord> getSalesrecords() {
		return this.salesrecords;
	}

	public void setSalesrecords(List<SalesRecord> salesrecords) {
		this.salesrecords = salesrecords;
	}

	public SalesRecord addSalesrecord(SalesRecord salesrecord) {
		getSalesrecords().add(salesrecord);
		salesrecord.setAccount(this);

		return salesrecord;
	}

	public SalesRecord removeSalesrecord(SalesRecord salesrecord) {
		getSalesrecords().remove(salesrecord);
		salesrecord.setAccount(null);

		return salesrecord;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", name=" + name + ", accountNum=" + accountNum + ", status=" + status + "]";
	}

	
}