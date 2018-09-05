package com.perfectchina.bns.common.utils;

import java.math.BigDecimal;

public class BigDecimalUtil {
    private BigDecimalUtil(){

    }
    // 加法
    public static BigDecimal add(Double v1,Double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2);
    }
    // 减法
    public static BigDecimal subtract(Double v1,Double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }
    // 乘法
    public static BigDecimal multiply(Double v1,Double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }
    // 除法
    public static BigDecimal divide(Double v1,Double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);
    }
}
