package com.perfectchina.bns.common.utils;

import org.apache.commons.lang3.StringUtils;

public class Const {
    public enum PinEnum{
        MEMBER(1,"MEMBER"),
        ONE_STAR(2,"ONE_STAR"),
        TWO_STAR(3,"TWO_STAR"),
        THREE_STAR(4,"THREE_STAR"),
        FOUR_STAR(5,"FOUR_STAR"),
        FIVE_STAR(6,"FIVE_STAR"),
        RUBY(7,"RUBY"),
        EMERALD(8,"EMERALD"),
        DIAMONDS(9,"DIAMONDS"),
        GOLD_DRILL(10,"GOLD_DRILL"),
        DOUBLE_GOLD_DRILL(11,"DOUBLE_GOLD_DRILL"),
        THREE_GOLD_DRILL(12,"THREE_GOLD_DRILL");

        private String value;
        private Integer code;

        PinEnum(Integer code, String value) {
            this.code = code;
            this.value = value;
        }

        public static PinEnum codeOf(Integer code){
            for (PinEnum pinEnum : values()){
                if (pinEnum.getCode() == code){
                    return pinEnum;
                }
            }
            throw new RuntimeException("没有此职级码！");
        }
        public static PinEnum valOf(String value){
            for (PinEnum pinEnum : values()){
                if (StringUtils.equals(pinEnum.getValue(),value)){
                    return pinEnum;
                }
            }
            throw new RuntimeException("没有此职级码！");
        }

        public String getValue() {
            return value;
        }

        public Integer getCode() {
            return code;
        }
    }
}
