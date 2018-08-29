package com.perfectchina.bns.model.reward;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/29
 * @Desc:  三金钻奖计算比率
 */
@Entity
@NamedQuery(name="TripleGoldenDiamondBonusRate.findAll", query="SELECT b FROM TripleGoldenDiamondBonusRate b")
public class TripleGoldenDiamondBonusRate {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private float tripleDiamondRate;  // 三钻 0.05%
    private float doubleDiamondRate;  // 双钻 0.05%

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

    public float getTripleDiamondRate() {
        return tripleDiamondRate;
    }

    public void setTripleDiamondRate(float tripleDiamondRate) {
        this.tripleDiamondRate = tripleDiamondRate;
    }

    public float getDoubleDiamondRate() {
        return doubleDiamondRate;
    }

    public void setDoubleDiamondRate(float doubleDiamondRate) {
        this.doubleDiamondRate = doubleDiamondRate;
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
