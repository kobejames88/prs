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

    @Column(columnDefinition="float default 0.00")
    private Float passUpOpv;

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
