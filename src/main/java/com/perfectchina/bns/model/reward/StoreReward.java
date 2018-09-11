package com.perfectchina.bns.model.reward;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

/**
 * creat by xb
 */
@ApiModel(value = "店铺奖励")
@Entity
@NamedQuery(name="StoreReward.findAll", query="SELECT a FROM StoreReward a")
public class StoreReward {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @ApiModelProperty("id")
    private Integer id;

    private String accountNum;

    private String serviceCenterNum;

    private float reword;

    private String createdBy;

    @Temporal(TemporalType.DATE)
    private Date creationDate;

    private String lastUpdatedBy;

    @Temporal(TemporalType.DATE)
    private Date lastUpdatedDate;

    @Temporal(TemporalType.DATE)
    private Date rewardDate;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public String getServiceCenterNum() {
        return serviceCenterNum;
    }

    public void setServiceCenterNum(String serviceCenterNum) {
        this.serviceCenterNum = serviceCenterNum;
    }

    public float getReword() {
        return reword;
    }

    public void setReword(float reword) {
        this.reword = reword;
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
