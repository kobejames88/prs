package com.perfectchina.bns.service.Enum;

import org.apache.commons.lang3.StringUtils;

public enum RewardType {
    ONCE_REWARD(1,"ONCE_REWARD"),
    DOUBLE_REWARD(2,"DOUBLE_REWARD"),
    TRIPLE_REWARD(3,"TRIPLE_REWARD"),
    QUADRUPLE_REWARD(4,"QUADRUPLE_REWARD");

    private String desc;
    private Integer code;

    RewardType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RewardType codeOf(Integer code){
        for (RewardType pin : values()){
            if (pin.getCode() == code){
                return pin;
            }
        }
        throw new RuntimeException("没有此类型！");
    }

    public static RewardType descOf(String desc){
        for (RewardType pin : values()){
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
