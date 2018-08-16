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

    private Float opv;

    private Float passUpOpv;

    private String levelLine;

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
