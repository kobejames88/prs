package com.perfectchina.bns.service.Enum;

import org.apache.commons.lang3.StringUtils;

public enum Pin {
    MEMBER(0,"MEMBER"),
    ONE_STAR(1,"ONE_STAR"),
    TWO_STAR(2,"TWO_STAR"),
    THREE_STAR(3,"THREE_STAR"),
    FOUR_STAR(4,"FOUR_STAR"),
    FIVE_STAR(5,"5_STAR"),
    Qualified_5_STAR(5,"Qualified_5_STAR"),
    NEW_5_STAR(5,"NEW_5_STAR"),
    BOTTOM_QUALIFIED_5_STAR(5,"BOTTOM_QUALIFIED_5_STAR"),
    RUBY(6,"RUBY"),
    EMERALD(7,"EMERALD"),
    DIAMOND(8,"DIAMOND"),
    GOLD_DIA(9,"GOLD_DIA"),
    DOU_GOLD_DIA(10,"DOU_GOLD_DIA"),
    TRI_GOLD_DIA(10,"TRI_GOLD_DIA");


    private String desc;
    private Integer code;

    Pin(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Pin codeOf(Integer code){
        for (Pin pin : values()){
            if (pin.getCode() == code){
                return pin;
            }
        }
        throw new RuntimeException("没有此职级！");
    }

    public static Pin descOf(String desc){
        for (Pin pin : values()){
            if (StringUtils.equals(pin.getDesc(),desc)){
                return pin;
            }
        }
        throw new RuntimeException("没有此职级！");
    }


    public String getDesc() {
        return desc;
    }

    public Integer getCode() {
        return code;
    }
}
