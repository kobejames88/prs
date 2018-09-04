package com.perfectchina.bns.model.reward;

import java.io.Serializable;

import javax.persistence.*;

import com.perfectchina.bns.model.Account;

import java.util.Date;


/**
 * The persistent class for the accountreward database table.
 * 
 */
@Entity
@NamedQuery(name="DiamondDividendBonusReward.findAll", query="SELECT a FROM DiamondDividendBonusReward a")
public class DiamondDividendBonusReward implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)	
	private long id;

	private String createdBy;

	@Temporal(TemporalType.DATE)
	private Date creationDate;

	private float bonusRate;  // 1 month for 1 point. 11 month 11 point. 12 month 12 * 1.2 = 14.4 point

	private Boolean hasBonus; // 财年度有6个月达标金钻（或以上职级），且4个月连续，才有资格积分
	
	private float amount;
	
	private String lastUpdatedBy;

	@Temporal(TemporalType.DATE)
	private Date lastUpdatedDate;

	private float ppv;

	@Temporal(TemporalType.DATE)
	private Date rewardDate;

	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="AccountId")
	private Account account;

	public DiamondDividendBonusReward() {
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


	public Boolean getHasBonus() {
		return hasBonus;
	}

	public void setHasBonus(Boolean hasBonus) {
		this.hasBonus = hasBonus;
	}

	
	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}


	public float getBonusRate() {
		return bonusRate;
	}

	public void setBonusRate(float bonusRate) {
		this.bonusRate = bonusRate;
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

	public float getPpv() {
		return ppv;
	}

	public void setPpv(float ppv) {
		this.ppv = ppv;
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
		return "AccountReward [id=" + id + ", createdBy=" + createdBy
				+ ", creationDate=" + creationDate 
				+ ", bonusRate=" + bonusRate + ", hasBonus=" + hasBonus
				+ ", amount="
				+ amount + ", lastUpdatedBy=" + lastUpdatedBy
				+ ", lastUpdatedDate=" + lastUpdatedDate 
				+ ", ppv=" + ppv + ", rewardDate=" + rewardDate
				+ ", account=" + account + "]";
	}
	




}