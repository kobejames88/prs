package com.perfectchina.bns.model.reward;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/14
 * @Desc: 红宝石奖 计算比率
 */
@Entity
@NamedQuery(name="RubyBonusRate.findAll", query="SELECT b FROM RubyBonusRate b")
public class RubyBonusRate {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Column(columnDefinition = "Float default 0.11")
    private  float firstRate;

    @Column(columnDefinition = "Float default 0.02")
    private  float secondRate;

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

    public float getFirstRate() {
        return firstRate;
    }

    public void setFirstRate(float firstRate) {
        this.firstRate = firstRate;
    }

    public float getSecondRate() {
        return secondRate;
    }

    public void setSecondRate(float secondRate) {
        this.secondRate = secondRate;
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
