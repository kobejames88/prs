package com.perfectchina.bns.service.reward;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

public interface DividendBonus {

//    public void calculateTotalIntegral(String pin);
//
//    public void hasQualification(Long accountId,String pin);
//
//    public BigDecimal getIntegral(int count);
//
//    public BigDecimal calculateDividendBonus();
    // 创建分红奖
    public void build(String pin);
}
