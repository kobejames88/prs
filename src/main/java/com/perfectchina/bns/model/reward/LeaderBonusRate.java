package com.perfectchina.bns.model.reward;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/14
 * @Desc: 领导奖 计算比率
 */
@Entity
@NamedQuery(name="LeaderBonusRate.findAll", query="SELECT b FROM LeaderBonusRate b")
public class LeaderBonusRate {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    //红宝石第一代比率
    @Column(columnDefinition = "Float default 0.11")
    private  float rubyFirstRate;

    //红宝石第二代比率
    @Column(columnDefinition = "Float default 0.02")
    private  float rubySecondRate;

    //翡翠第二+代比率
    @Column(columnDefinition = "Float default 0.07")
    private float emeraldRate;

    //钻石第二+代比率
    @Column(columnDefinition = "Float default 0.03")
    private  float diamondRate;

    //金钻第二+代比率
    @Column(columnDefinition = "Float default 0.01")
    private float goldenDiamondgRate;

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

    public float getRubyFirstRate() {
        return rubyFirstRate;
    }

    public void setRubyFirstRate(float rubyFirstRate) {
        this.rubyFirstRate = rubyFirstRate;
    }

    public float getRubySecondRate() {
        return rubySecondRate;
    }

    public void setRubySecondRate(float rubySecondRate) {
        this.rubySecondRate = rubySecondRate;
    }

    public float getEmeraldRate() {
        return emeraldRate;
    }

    public void setEmeraldRate(float emeraldRate) {
        this.emeraldRate = emeraldRate;
    }

    public float getDiamondRate() {
        return diamondRate;
    }

    public void setDiamondRate(float diamondRate) {
        this.diamondRate = diamondRate;
    }

    public float getGoldenDiamondgRate() {
        return goldenDiamondgRate;
    }

    public void setGoldenDiamondgRate(float goldenDiamondgRate) {
        this.goldenDiamondgRate = goldenDiamondgRate;
    }
}
