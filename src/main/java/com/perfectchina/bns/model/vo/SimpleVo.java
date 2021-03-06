package com.perfectchina.bns.model.vo;

import java.util.List;

/**
 * QualifiedFiveStar
 */
public class SimpleVo {
	private static final long serialVersionUID = 1L;

    private int levelNum;

    private String name;

    private String accountNum;

    private Float ppv;

    private Float money;

    private List<SimpleVo> children;

    public Float getMoney() {
        return money;
    }

    public void setMoney(Float money) {
        this.money = money;
    }

    public List<SimpleVo> getChildren() {
        return children;
    }

    public void setChildren(List<SimpleVo> children) {
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

    public Float getPpv() {
        return ppv;
    }

    public void setPpv(Float ppv) {
        this.ppv = ppv;
    }

}
