package com.perfectchina.bns.model;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the customer differential bonus database table.
 * 
 */
@Entity
@NamedQuery(name="CustomerDifferentialBonus.findAll", query="SELECT a FROM CustomerDifferentialBonus a")
public class CustomerDifferentialBonus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)	
	private long id;

	private String createdBy;

	@Temporal(TemporalType.DATE)
	private Date creationDate;

	private float aopv;

	private float bonusRateLvl1;  // 0 - 500 = 0%

	private float bonusRateLvl2;  // 501 - 9000 = 6%

	private float bonusRateLvl3;
	
	private float bonusRateLvl4;
		
	private Boolean hasBonus;  // flag for customer has bonus or not

	@Transient 
	private float distributorSalesAmount;
	
	private float amountLvl1;  // received amount for Level 1 bonus rate  

	private float amountLvl2;  // received amount for Level 2 bonus rate  

	private float amountLvl3;  // received amount for Level 3 bonus rate  

	private float amountLvl4;  // received amount for Level 4 bonus rate  

	private float amountTotal;  // received amount for all the levels  
	
	private String lastUpdatedBy;

	@Temporal(TemporalType.DATE)
	private Date lastUpdatedDate;

	@Temporal(TemporalType.DATE)
	private Date rewardDate;

	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="AccountId")
	private Account account;

	public CustomerDifferentialBonus() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public float getAopv() {
		return aopv;
	}

	public void setAopv(float aopv) {
		this.aopv = aopv;
	}

	public float getBonusRateLvl1() {
		return bonusRateLvl1;
	}

	public void setBonusRateLvl1(float bonusRateLvl1) {
		this.bonusRateLvl1 = bonusRateLvl1;
	}

	public float getBonusRateLvl2() {
		return bonusRateLvl2;
	}

	public void setBonusRateLvl2(float bonusRateLvl2) {
		this.bonusRateLvl2 = bonusRateLvl2;
	}

	public float getBonusRateLvl3() {
		return bonusRateLvl3;
	}

	public void setBonusRateLvl3(float bonusRateLvl3) {
		this.bonusRateLvl3 = bonusRateLvl3;
	}

	public float getBonusRateLvl4() {
		return bonusRateLvl4;
	}

	public void setBonusRateLvl4(float bonusRateLvl4) {
		this.bonusRateLvl4 = bonusRateLvl4;
	}

	public float getAmountLvl1() {
		return amountLvl1;
	}

	public void setAmountLvl1(float amountLvl1) {
		this.amountLvl1 = amountLvl1;
	}

	public float getAmountLvl2() {
		return amountLvl2;
	}

	public void setAmountLvl2(float amountLvl2) {
		this.amountLvl2 = amountLvl2;
	}

	public float getAmountLvl3() {
		return amountLvl3;
	}

	public void setAmountLvl3(float amountLvl3) {
		this.amountLvl3 = amountLvl3;
	}

	public float getAmountLvl4() {
		return amountLvl4;
	}

	public void setAmountLvl4(float amountLvl4) {
		this.amountLvl4 = amountLvl4;
	}

	public float getAmountTotal() {
		return amountTotal;
	}

	public void setAmountTotal(float amountTotal) {
		this.amountTotal = amountTotal;
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

	public Boolean getHasBonus() {
		return hasBonus;
	}

	public void setHasBonus(Boolean hasBonus) {
		this.hasBonus = hasBonus;
	}

	
	public float getDistributorSalesAmount() {
		return distributorSalesAmount;
	}

	public void setDistributorSalesAmount(float distributorSalesAmount) {
		this.distributorSalesAmount = distributorSalesAmount;
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

	public Date getRewardDate() {
		return this.rewardDate;
	}

	public void setRewardDate(Date rewardDate) {
		this.rewardDate = rewardDate;
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
		return "CustomerDifferentialBonus [id=" + id + ", createdBy=" + createdBy + ", creationDate=" + creationDate
				+ ", aopv=" + aopv + ", bonusRateLvl1=" + bonusRateLvl1 + ", bonusRateLvl2=" + bonusRateLvl2
				+ ", bonusRateLvl3=" + bonusRateLvl3 + ", bonusRateLvl4=" + bonusRateLvl4 + ", hasBonus=" + hasBonus
				+ ", distributorSalesAmount=" + distributorSalesAmount + ", amountLvl1=" + amountLvl1 + ", amountLvl2="
				+ amountLvl2 + ", amountLvl3=" + amountLvl3 + ", amountLvl4=" + amountLvl4 + ", amountTotal="
				+ amountTotal + ", lastUpdatedBy=" + lastUpdatedBy + ", lastUpdatedDate=" + lastUpdatedDate
				+ ", rewardDate=" + rewardDate + "]";
	}


}