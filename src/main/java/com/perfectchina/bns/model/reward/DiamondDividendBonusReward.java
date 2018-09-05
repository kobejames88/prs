package com.perfectchina.bns.model.reward;

import java.io.Serializable;

import javax.persistence.*;

import com.perfectchina.bns.model.Account;

import java.math.BigDecimal;
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

	private BigDecimal bonusRate;  // 积分

	private String createdBy;

	@Temporal(TemporalType.DATE)
	private Date creationDate;

	private BigDecimal amount;
	
	private String lastUpdatedBy;

	@Temporal(TemporalType.DATE)
	private Date lastUpdatedDate;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBonusRate() {
        return bonusRate;
    }

    public void setBonusRate(BigDecimal bonusRate) {
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
				+ ", bonusRate=" + bonusRate
				+ ", amount="
				+ amount + ", lastUpdatedBy=" + lastUpdatedBy
				+ ", lastUpdatedDate=" + lastUpdatedDate 
				+ ", account=" + account + "]";
	}
	




}