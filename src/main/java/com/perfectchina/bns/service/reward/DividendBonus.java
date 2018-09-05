package com.perfectchina.bns.service.reward;

import java.math.BigDecimal;

public interface DividendBonus {
    // 计算每人积分和总积分
    public void calculateTotalIntegral();
    // 判断是否拥有资格
    public void hasQualification(Long accountId);
    // 获取总积分
    public BigDecimal getIntegral(int count);
    // 计算分红奖
    public BigDecimal calculateDividendBonus();
}
