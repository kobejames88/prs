package com.perfectchina.bns.model.treenode;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * DoubleGoldDiamondNetTreeNode
 */
@Entity
@Table(name = "DoubleGoldDiamondNetTreeNode")
@NamedQuery(name="DoubleGoldDiamondNetTreeNode.findAll", query="SELECT a FROM DoubleGoldDiamondNetTreeNode a")
public class DoubleGoldDiamondNetTreeNode extends TreeNode {
	private static final long serialVersionUID = 1L;

    private String levelLine;

    private String pin;

    private String reward;

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
