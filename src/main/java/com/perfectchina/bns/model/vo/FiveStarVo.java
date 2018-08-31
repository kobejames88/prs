package com.perfectchina.bns.model.vo;

import java.util.List;

/**
 * QualifiedFiveStar
 */
public class FiveStarVo {
	private static final long serialVersionUID = 1L;

    private int levelNum;

    private String name;

    private String accountNum;

//    private int qualifiedLine;

    private Float ppv;

    private Float gpv;

    private Float opv;

    private int pin;

    private int maxPin;

    private List<FiveStarVo> children;

    public List<FiveStarVo> getChildren() {
        return children;
    }

    public void setChildren(List<FiveStarVo> children) {
        this.children = children;
    }

    public int getLevelNum() {
        return levelNum;
    }

    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

//    public int getQualifiedLine() {
//        return qualifiedLine;
//    }
//
//    public void setQualifiedLine(int qualifiedLine) {
//        this.qualifiedLine = qualifiedLine;
//    }

    public Float getPpv() {
        return ppv;
    }

    public void setPpv(Float ppv) {
        this.ppv = ppv;
    }

    public Float getGpv() {
        return gpv;
    }

    public void setGpv(Float gpv) {
        this.gpv = gpv;
    }

    public Float getOpv() {
        return opv;
    }

    public void setOpv(Float opv) {
        this.opv = opv;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public int getMaxPin() {
        return maxPin;
    }

    public void setMaxPin(int maxPin) {
        this.maxPin = maxPin;
    }
}
