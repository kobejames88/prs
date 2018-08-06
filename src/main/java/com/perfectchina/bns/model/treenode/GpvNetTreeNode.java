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

	private Float opv;  // Opv for the current month, opv = child's opv + thisNode ppv

	private Float aopv; // Aopv = opv + aopvLastMonth

	// last month accumulated OPV
	private Float aopvLastMonth; // Last month accumulated OPV

	private String pin;

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

	public Float getAopv() {
		return aopv;
	}

	public void setAopv(Float aopv) {
		this.aopv = aopv;
	}

	public Float getAopvLastMonth() {
		return aopvLastMonth;
	}

	public void setAopvLastMonth(Float aopvLastMonth) {
		this.aopvLastMonth = aopvLastMonth;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	@Override
	public String toString() {
		return "OpvNetTreeNode [ppv=" + ppv + ", gpv=" + gpv + ", toString()=" + super.toString() + "]";
	}


	
}
