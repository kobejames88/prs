package com.perfectchina.bns.model.vo;

import java.util.List;

/**
 * QualifiedFiveStar
 */
public class QualifiedFiveStarVo{
	private static final long serialVersionUID = 1L;

//    private Long id;

    private int levelNum;

    private String name;

    private String accountNum;

    private int qualifiedLine;

    private Float ppv;

    private Float gpv;

    private Float opv;

    private Float passUpGpv;

    private Float fiveStarIntegral;

    private Float borrowPoints;

	private Float borrowedPoints;

    private int pin;

    private int maxPin;

    private List<QualifiedFiveStarVo> children;

    public List<QualifiedFiveStarVo> getChildren() {
        return children;
    }

    public void setChildren(List<QualifiedFiveStarVo> children) {
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

    public int getQualifiedLine() {
        return qualifiedLine;
    }

    public void setQualifiedLine(int qualifiedLine) {
        this.qualifiedLine = qualifiedLine;
    }

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

    public Float getPassUpGpv() {
        return passUpGpv;
    }

    public void setPassUpGpv(Float passUpGpv) {
        this.passUpGpv = passUpGpv;
    }

    public Float getFiveStarIntegral() {
        return fiveStarIntegral;
    }

    public void setFiveStarIntegral(Float fiveStarIntegral) {
        this.fiveStarIntegral = fiveStarIntegral;
    }

    public Float getBorrowPoints() {
        return borrowPoints;
    }

    public void setBorrowPoints(Float borrowPoints) {
        this.borrowPoints = borrowPoints;
    }

    public Float getBorrowedPoints() {
        return borrowedPoints;
    }

    public void setBorrowedPoints(Float borrowedPoints) {
        this.borrowedPoints = borrowedPoints;
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
