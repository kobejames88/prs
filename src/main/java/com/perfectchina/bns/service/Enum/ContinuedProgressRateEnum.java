package com.perfectchina.bns.service.Enum;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author: chenhuahai
 * @Date: 2018/9/5
 * @Desc: 持续进步奖 各职级算分基数
 */

public enum ContinuedProgressRateEnum {

    //各职级算分基数
    RUBY(1,"RUBY"),
    EMERALD(2,"EMERALD"),
    DIAMOND(3,"DIAMOND"),
    GOLD_DIA(4,"GOLD_DIA"),
    DOU_GOLD_DIA(4,"DOU_GOLD_DIA"),
    TRI_GOLD_DIA(4,"TRI_GOLD_DIA"),
    //新晋连续有效月数
    NEWCOUNT(12,"NEWCOUNT"),
    OLDCOUNT(12,"OLDCOUNT");


    //职级
    private String desc;
    //计分基数
    private Integer rate;

    ContinuedProgressRateEnum(Integer rate, String desc) {
        this.rate = rate;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getRate() {
        return rate;
    }

    /**
     * 输入职级查找该职级的计分基数
     * @param desc
     * @return
     */
    public static Integer rateOf(String desc){
        for (ContinuedProgressRateEnum continuedProgressRateEnum : values()){
            if (StringUtils.equals(continuedProgressRateEnum.getDesc(),desc)){
                return continuedProgressRateEnum.getRate();
            }
        }
        throw new RuntimeException("没有此职级！");
    }

}
