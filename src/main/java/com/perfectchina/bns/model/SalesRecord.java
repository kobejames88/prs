package com.perfectchina.bns.model;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the salesrecord database table.
 * 
 */
@Entity
@Table(name = "SalesRecord")
@NamedQuery(name="SalesRecord.findAll", query="SELECT s FROM SalesRecord s")
public class SalesRecord implements Serializable {
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

	private String orderNum;

	private float retailSalesAmount;
	
	private float salesAmount;

	@Temporal(TemporalType.DATE)
	private Date salesDate;

	private float salesPV;

	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="accountId")
	private Account account;

	public SalesRecord() {
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

	public String getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public float getRetailSalesAmount() {
		return retailSalesAmount;
	}

	public void setRetailSalesAmount(float retailSalesAmount) {
		this.retailSalesAmount = retailSalesAmount;
	}

	public float getSalesAmount() {
		return this.salesAmount;
	}

	public void setSalesAmount(float salesAmount) {
		this.salesAmount = salesAmount;
	}

	public Date getSalesDate() {
		return this.salesDate;
	}

	public void setSalesDate(Date salesDate) {
		this.salesDate = salesDate;
	}


	public float getSalesPV() {
		return salesPV;
	}

	public void setSalesPV(float salesPV) {
		this.salesPV = salesPV;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	

	@Override
	public String toString() {
		return "SalesRecord [id=" + id + ", createdBy=" + createdBy
				+ ", creationDate=" + creationDate + ", lastUpdatedBy="
				+ lastUpdatedBy + ", lastUpdatedDate=" + lastUpdatedDate
				+ ", orderNum=" + orderNum + ", retailSalesAmount="
				+ retailSalesAmount + ", salesAmount=" + salesAmount
				+ ", salesDate=" + salesDate
				+ ", salesPV=" + salesPV + ", account=" + account + "]";
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