package com.perfectchina.bns.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

/**
 * creat by xb
 * 服务中心
 */
@ApiModel(value = "服务中心信息")
@Entity
@NamedQuery(name="MemberLog.findAll", query="SELECT s FROM MemberLog s")
public class MemberLog {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @ApiModelProperty("id")
     private Integer id;
    //服务中心编号
    @ApiModelProperty(value = "服务中心编号")
    private String serviceCenterNum;
    //会员账号
    @ApiModelProperty(value = "会员账号")
    private String  accountNum;

    @ApiModelProperty(value = "是否违规")
    private Boolean violation;


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

    public Boolean getViolation() {
        return violation;
    }

    public void setViolation(Boolean violation) {
        this.violation = violation;
    }
}
