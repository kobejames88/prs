package com.perfectchina.bns.common.utils;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/24
 * @Desc:
 */

public class ManthUtils {

    //四舍五入保留两位小数
    public static  float round(float reward){
        return Math.round(reward*100)/100F;
    }
}
