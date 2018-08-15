package com.perfectchina.bns.model.reward;

import com.perfectchina.bns.model.treenode.TreeNode;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/14
 * @Desc: 领导奖
 */
@Entity
@Table(name = "LeaderBonus")
@NamedQuery(name="LeaderBonus.findAll", query="SELECT a FROM LeaderBonus a")
public class LeaderBonus extends TreeNode{

    private float rubyReward;


    private Float passUpGpv;

    private int qualifiedLine;

    private Float gpv;

    @Column(columnDefinition="float default 0.00")
    private Float fiveStarIntegral;

    @Column(columnDefinition="float default 0.00")
    private Float borrowPoints;

    @Column(columnDefinition="float default 0.00")
    private Float borrowedPoints;

    @Column(columnDefinition="bigint default 0")
    private Long borrowTo;

    private Boolean hasAsteriskNode;

    @Column(columnDefinition="float default 0")
    private Float asteriskNodePoints;

    private String levelLine;

    private String pin;


    @Temporal(TemporalType.DATE)
    private Date rewardDate;

    private String createdBy;

    @Temporal(TemporalType.DATE)
    private Date creationDate;

    private String lastUpdatedBy;

    @Temporal(TemporalType.DATE)
    private Date lastUpdatedDate;



    public Float getPassUpGpv() {
        return passUpGpv;
    }

    public void setPassUpGpv(Float passUpGpv) {
        this.passUpGpv = passUpGpv;
    }

    public int getQualifiedLine() {
        return qualifiedLine;
    }

    public void setQualifiedLine(int qualifiedLine) {
        this.qualifiedLine = qualifiedLine;
    }

    public Float getGpv() {
        return gpv;
    }

    public void setGpv(Float gpv) {
        this.gpv = gpv;
    }

    public Float getFiveStarIntegral() {
        return fiveStarIntegral;
    }

    public void setFiveStarIntegral(Float fiveStarIntegral) {
        this.fiveStarIntegral = fiveStarIntegral;
    }

    public Float getBorrowPoints() {
        return borrowPoints;
    }

    public void setBorrowPoints(Float borrowPoints) {
        this.borrowPoints = borrowPoints;
    }

    public Float getBorrowedPoints() {
        return borrowedPoints;
    }

    public void setBorrowedPoints(Float borrowedPoints) {
        this.borrowedPoints = borrowedPoints;
    }

    public Long getBorrowTo() {
        return borrowTo;
    }

    public void setBorrowTo(Long borrowTo) {
        this.borrowTo = borrowTo;
    }

    public Boolean getHasAsteriskNode() {
        return hasAsteriskNode;
    }

    public void setHasAsteriskNode(Boolean hasAsteriskNode) {
        this.hasAsteriskNode = hasAsteriskNode;
    }

    public Float getAsteriskNodePoints() {
        return asteriskNodePoints;
    }

    public void setAsteriskNodePoints(Float asteriskNodePoints) {
        this.asteriskNodePoints = asteriskNodePoints;
    }

    public String getLevelLine() {
        return levelLine;
    }

    public void setLevelLine(String levelLine) {
        this.levelLine = levelLine;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
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

    public float getRubyReward() {
        return rubyReward;
    }

    public void setRubyReward(float rubyReward) {
        this.rubyReward = rubyReward;
    }
}
