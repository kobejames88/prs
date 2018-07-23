package com.perfectchina.bns.model.treenode;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

/**
 * The persistent class for the Opv Net Tree Node database table. This network used for
 * finding the corresponding qualified 5 star pin 
 * 
 */
@Entity
@Table(name = "OpvNetTreeNode")
@NamedQuery(name="OpvNetTreeNode.findAll", query="SELECT a FROM OpvNetTreeNode a")
public class OpvNetTreeNode extends TreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Current month PV
	private Float ppv;  // personal PV

	private Float opv;  // Opv for the current month, opv = child's opv + thisNode ppv

	private Float aopv; // Aopv = opv + aopvLastMonth  
	
	// last month accumulated OPV
	private Float aopvLastMonth; // Last month accumulated OPV 
	
	public Float getPpv() {
		return ppv;
	}

	public void setPpv(Float ppv) {
		this.ppv = ppv;
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

	@Override
	public String toString() {
		return "OpvNetTreeNode [ppv=" + ppv + ", opv=" + opv + ", aopv=" + aopv + ", aopvLastMonth=" + aopvLastMonth
				+ ", toString()=" + super.toString() + "]";
	}


	
}
