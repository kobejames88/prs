package com.perfectchina.bns.model.reward;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/7
 * @Desc:
 */
@Entity
@NamedQuery(name="DistributorDifferentialBonusRate.findAll", query="SELECT b FROM DistributorDifferentialBonusRate b")
public class DistributorDifferentialBonusRate implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private float minOpv;
    private float maxOpv;

    private float bonusRate;


    private String createdBy;

    @Temporal(TemporalType.DATE)
    private Date creationDate;

    @Temporal(TemporalType.DATE)
    private Date effectiveFrom;

    @Temporal(TemporalType.DATE)
    private Date effectiveTo;

    private String lastUpdatedBy;

    @Temporal(TemporalType.DATE)
    private Date lastUpdatedDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getMinOpv() {
        return minOpv;
    }

    public void setMinOpv(float minOpv) {
        this.minOpv = minOpv;
    }

    public float getMaxOpv() {
        return maxOpv;
    }

    public void setMaxOpv(float maxOpv) {
        this.maxOpv = maxOpv;
    }

    public float getBonusRate() {
        return bonusRate;
    }

    public void setBonusRate(float bonusRate) {
        this.bonusRate = bonusRate;
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

    public Date getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(Date effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public Date getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(Date effectiveTo) {
        this.effectiveTo = effectiveTo;
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
