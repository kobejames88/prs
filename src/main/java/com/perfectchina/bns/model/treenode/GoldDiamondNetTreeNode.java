package com.perfectchina.bns.model.treenode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * GoldDiamondNetTreeNode
 */
@Entity
@Table(name = "GoldDiamondNetTreeNode")
@NamedQuery(name="GoldDiamondNetTreeNode.findAll", query="SELECT a FROM GoldDiamondNetTreeNode a")
public class GoldDiamondNetTreeNode extends TreeNode {
	private static final long serialVersionUID = 1L;

    private Float ppv;

    private Float gpv;

    private Float opv;

    private Float passUpOpv;

    private Float mergePoints;

    private int goldDiamondLine;

    private String levelLine;

    private String reward;

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

    public int getGoldDiamondLine() {
        return goldDiamondLine;
    }

    public void setGoldDiamondLine(int goldDiamondLine) {
        this.goldDiamondLine = goldDiamondLine;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public Float getMergePoints() {
        return mergePoints;
    }

    public void setMergePoints(Float mergePoints) {
        this.mergePoints = mergePoints;
    }

    public String getLevelLine() {
        return levelLine;
    }

    public void setLevelLine(String levelLine) {
        this.levelLine = levelLine;
    }

    public Float getPassUpOpv() {
        return passUpOpv;
    }

    public void setPassUpOpv(Float passUpOpv) {
        this.passUpOpv = passUpOpv;
    }

    public Float getOpv() {
        return opv;
    }

    public void setOpv(Float opv) {
        this.opv = opv;
    }
}
