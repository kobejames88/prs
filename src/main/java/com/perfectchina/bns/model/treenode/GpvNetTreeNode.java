package com.perfectchina.bns.model.treenode;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Group PV
 */
@Entity
@Table(name = "GpvNetTreeNode")
@NamedQuery(name="GpvNetTreeNode.findAll", query="SELECT a FROM GpvNetTreeNode a")
public class GpvNetTreeNode extends TreeNode {
	private static final long serialVersionUID = 1L;

	// Current month gpv
	private Float ppv;  // personal PV

	private Float gpv;  // Individuals and subordinates do not reach the five-star total PPV sum


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

	@Override
	public String toString() {
		return "OpvNetTreeNode [ppv=" + ppv + ", gpv=" + gpv + ", toString()=" + super.toString() + "]";
	}


	
}
