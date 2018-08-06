package com.perfectchina.bns.model;

import com.perfectchina.bns.model.treenode.TreeNode;

import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the customer differential bonus database table.
 * 
 */
@Entity
@Table(name = "CustomerBonusNet")
@NamedQuery(name="CustomerBonusNet.findAll", query="SELECT a FROM CustomerBonusNet a")
public class CustomerBonusNet extends TreeNode {

	private static final long serialVersionUID = 1L;

	private float opv;

	private float aopv;

	private float aopvLastMonth;

	// reward = amountTotal - all child.amountTotal
	private float reward;

	// received amount for all the levels
	private float amountTotal;

	@Temporal(TemporalType.DATE)
	private Date rewardDate;

	private String createdBy;

	@Temporal(TemporalType.DATE)
	private Date creationDate;

	private String lastUpdatedBy;

	@Temporal(TemporalType.DATE)
	private Date lastUpdatedDate;

	public CustomerBonusNet() {
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public float getAopvLastMonth() {
		return aopvLastMonth;
	}

	public void setAopvLastMonth(float aopvLastMonth) {
		this.aopvLastMonth = aopvLastMonth;
	}

	public float getAopv() {
		return aopv;
	}

	public void setAopv(float aopv) {
		this.aopv = aopv;
	}

	public float getOpv() {
		return opv;
	}

	public void setOpv(float opv) {
		this.opv = opv;
	}

	public float getReward() {
		return reward;
	}

	public void setReward(float reward) {
		this.reward = reward;
	}

	public float getAmountTotal() {
		return amountTotal;
	}

	public void setAmountTotal(float amountTotal) {
		this.amountTotal = amountTotal;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public Date getRewardDate() {
		return rewardDate;
	}

	public void setRewardDate(Date rewardDate) {
		this.rewardDate = rewardDate;
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