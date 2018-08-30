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

    private Float opv;

    private Float passUpOpv;

    private Float mergePoints;

    private String levelLine;

    private String reward;

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
