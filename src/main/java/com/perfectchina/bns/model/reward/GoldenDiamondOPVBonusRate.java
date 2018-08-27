package com.perfectchina.bns.model.reward;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/23
 * @Desc: 金钻平级奖计算比率
 */
@Entity
@NamedQuery(name="GoldenDiamondOPVBonusRate.findAll", query="SELECT b FROM GoldenDiamondOPVBonusRate b")
public class GoldenDiamondOPVBonusRate {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private float bonusRateLvl1;  // 第一代 1%

    private float bonusRateLvl2;  // 第二代 0.5%

    private float bonusRateLvl3;  //第三代 0.25%

    private float bonusRateLvl4;  //第四代 0.25%

    private float bonusRateLvl5;  //第五代 0.1%
    private float bonusRateLvl6;  //第六代 0.1%
    private float bonusRateLvl7;  //第七代 0.1%
    private float bonusRateLvl8;  //第八代 0.1%


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

    public float getBonusRateLvl1() {
        return bonusRateLvl1;
    }

    public void setBonusRateLvl1(float bonusRateLvl1) {
        this.bonusRateLvl1 = bonusRateLvl1;
    }

    public float getBonusRateLvl2() {
        return bonusRateLvl2;
    }

    public void setBonusRateLvl2(float bonusRateLvl2) {
        this.bonusRateLvl2 = bonusRateLvl2;
    }

    public float getBonusRateLvl3() {
        return bonusRateLvl3;
    }

    public void setBonusRateLvl3(float bonusRateLvl3) {
        this.bonusRateLvl3 = bonusRateLvl3;
    }

    public float getBonusRateLvl4() {
        return bonusRateLvl4;
    }

    public void setBonusRateLvl4(float bonusRateLvl4) {
        this.bonusRateLvl4 = bonusRateLvl4;
    }

    public float getBonusRateLvl5() {
        return bonusRateLvl5;
    }

    public void setBonusRateLvl5(float bonusRateLvl5) {
        this.bonusRateLvl5 = bonusRateLvl5;
    }

    public float getBonusRateLvl6() {
        return bonusRateLvl6;
    }

    public void setBonusRateLvl6(float bonusRateLvl6) {
        this.bonusRateLvl6 = bonusRateLvl6;
    }

    public float getBonusRateLvl7() {
        return bonusRateLvl7;
    }

    public void setBonusRateLvl7(float bonusRateLvl7) {
        this.bonusRateLvl7 = bonusRateLvl7;
    }

    public float getBonusRateLvl8() {
        return bonusRateLvl8;
    }

    public void setBonusRateLvl8(float bonusRateLvl8) {
        this.bonusRateLvl8 = bonusRateLvl8;
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
