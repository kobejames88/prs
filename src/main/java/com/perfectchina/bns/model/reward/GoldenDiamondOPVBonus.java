package com.perfectchina.bns.model.reward;

import com.perfectchina.bns.model.Account;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/23
 * @Desc: 金钻平级奖
 */

@Entity
@NamedQuery(name="GoldenDiamondOPVBonus.findAll", query="SELECT a FROM GoldenDiamondOPVBonus a")
public class GoldenDiamondOPVBonus implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private float opv;

    //bi-directional many-to-one association to Account
    @ManyToOne
    @JoinColumn(name="AccountId")
    private Account account;

    private  String snapshotDate;

    private  float reward;


    private String createdBy;

    @Temporal(TemporalType.DATE)
    private Date creationDate;

    private String lastUpdatedBy;

    @Temporal(TemporalType.DATE)
    private Date lastUpdatedDate;

    @Temporal(TemporalType.DATE)
    private Date rewardDate;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getOpv() {
        return opv;
    }

    public void setOpv(float opv) {
        this.opv = opv;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(String snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public float getReward() {
        return reward;
    }

    public void setReward(float reward) {
        this.reward = reward;
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

    public Date getRewardDate() {
        return rewardDate;
    }

    public void setRewardDate(Date rewardDate) {
        this.rewardDate = rewardDate;
    }
}