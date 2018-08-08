package com.perfectchina.bns.model.reward;

import com.perfectchina.bns.model.treenode.TreeNode;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/7
 * @Desc:
 */
@Entity
@Table(name = "DistributorBonus")
@NamedQuery(name="DistributorBonus.findAll", query="SELECT a FROM DistributorBonus a")
public class DistributorBonus extends TreeNode {
    private static final long serialVersionUID = 1L;

    private float opv;

    private float gpv;

    private float aopvLastMonth;

    // reward = gpv * (rate - 12%)
    private float reward;

    // new 5star pass up
    private float temporaryReward;

    @Temporal(TemporalType.DATE)
    private Date rewardDate;

    private String createdBy;

    @Temporal(TemporalType.DATE)
    private Date creationDate;

    private String lastUpdatedBy;

    @Temporal(TemporalType.DATE)
    private Date lastUpdatedDate;

    public float getOpv() {
        return opv;
    }

    public void setOpv(float opv) {
        this.opv = opv;
    }

    public float getGpv() {
        return gpv;
    }

    public void setGpv(float gpv) {
        this.gpv = gpv;
    }

    public float getAopvLastMonth() {
        return aopvLastMonth;
    }

    public void setAopvLastMonth(float aopvLastMonth) {
        this.aopvLastMonth = aopvLastMonth;
    }

    public float getReward() {
        return reward;
    }

    public void setReward(float reward) {
        this.reward = reward;
    }

    public float getTemporaryReward() {
        return temporaryReward;
    }

    public void setTemporaryReward(float temporaryReward) {
        this.temporaryReward = temporaryReward;
    }

    public Date getRewardDate() {
        return rewardDate;
    }

    public void setRewardDate(Date rewardDate) {
        this.rewardDate = rewardDate;
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
}
