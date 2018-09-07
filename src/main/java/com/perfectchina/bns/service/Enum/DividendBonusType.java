package com.perfectchina.bns.service.Enum;

import org.apache.commons.lang3.StringUtils;

public enum DividendBonusType {
    DIAMOND(1,"DIAMOND"),
    GOLD_DIA(2,"GOLD_DIA");


    private String desc;
    private Integer code;

    DividendBonusType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DividendBonusType codeOf(Integer code){
        for (DividendBonusType pin : values()){
            if (pin.getCode() == code){
                return pin;
            }
        }
        throw new RuntimeException("没有此类型！");
    }

    public static DividendBonusType descOf(String desc){
        for (DividendBonusType pin : values()){
            if (StringUtils.equals(pin.getDesc(),desc)){
                return pin;
            }
        }
        throw new RuntimeException("没有此类型！");
    }


    public String getDesc() {
        return desc;
    }

    public Integer getCode() {
        return code;
    }
}
