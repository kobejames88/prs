package com.perfectchina.bns.model.treenode;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * DoubleGoldDiamondNetTreeNode
 */
@Entity
@Table(name = "DoubleGoldDiamondNetTreeNode")
@NamedQuery(name="DoubleGoldDiamondNetTreeNode.findAll", query="SELECT a FROM DoubleGoldDiamondNetTreeNode a")
public class DoubleGoldDiamondNetTreeNode extends TreeNode {
	private static final long serialVersionUID = 1L;

    private String levelLine;

    private Float passUpOpv;

    private Float ppv;

    private Float gpv;

    private Float opv;

    private String pin;

    private String reward;

    private BigDecimal rewardBonus;

    private int doubleGoldDiamondLine;

    public BigDecimal getRewardBonus() {
        return rewardBonus;
    }

    public void setRewardBonus(BigDecimal rewardBonus) {
        this.rewardBonus = rewardBonus;
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

    public int getDoubleGoldDiamondLine() {
        return doubleGoldDiamondLine;
    }

    public void setDoubleGoldDiamondLine(int doubleGoldDiamondLine) {
        this.doubleGoldDiamondLine = doubleGoldDiamondLine;
    }

    public Float getOpv() {
        return opv;
    }

    public void setOpv(Float opv) {
        this.opv = opv;
    }

    public Float getPassUpOpv() {
        return passUpOpv;
    }

    public void setPassUpOpv(Float passUpOpv) {
        this.passUpOpv = passUpOpv;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getLevelLine() {
        return levelLine;
    }

    public void setLevelLine(String levelLine) {
        this.levelLine = levelLine;
    }

}
