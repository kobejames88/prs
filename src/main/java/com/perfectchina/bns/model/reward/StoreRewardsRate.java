package com.perfectchina.bns.model.reward;




import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

/**
 * creat by xb
 * 店铺奖励汇率
 */
@ApiModel(value = "店铺奖励汇率")
@Entity
@NamedQuery(name="StoreRewardsRate.findAll", query="SELECT a FROM StoreRewardsRate a")
public class StoreRewardsRate {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @ApiModelProperty("id")
    private Integer id;
    @ApiModelProperty("帮扶政策开始时间")
    private Date startTime;
    @ApiModelProperty("帮扶政策结束时间")
    private Date endTime;
    @ApiModelProperty("基本奖励汇率")
    private float basicRate;
    @ApiModelProperty("不违规奖励汇率")
    private float violationRate;
    @ApiModelProperty("帮扶政策汇率")
    private float supportRate;


    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getBasicRate() {
        return basicRate;
    }

    public void setBasicRate(float basicRate) {
        this.basicRate = basicRate;
    }

    public float getViolationRate() {
        return violationRate;
    }

    public void setViolationRate(float violationRate) {
        this.violationRate = violationRate;
    }

    public float getSupportRate() {
        return supportRate;
    }

    public void setSupportRate(float supportRate) {
        this.supportRate = supportRate;
    }
}
