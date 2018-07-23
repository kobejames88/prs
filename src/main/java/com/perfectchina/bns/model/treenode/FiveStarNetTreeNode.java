package com.perfectchina.bns.model.treenode;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

/**
 * The persistent class for the account database table.
 * 
 */
@Entity
@Table(name = "FiveStarNetTreeNode")
@NamedQuery(name="FiveStarNetTreeNode.findAll", query="SELECT a FROM FiveStarNetTreeNode a")
public class FiveStarNetTreeNode extends TreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String snapshotDate; // yyyyMM
	
	private Float ppv;
	private Float opv;   // opv for current month
	private Float aopv;  // aopv = current month opv + last month accumulated opv
	private Boolean qualified;
	private String pin;

	public String getSnapshotDate() {
		return snapshotDate;
	}

	public void setSnapshotDate(String snapshotDate) {
		this.snapshotDate = snapshotDate;
	}


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

	public Boolean getQualified() {
		return qualified;
	}

	public void setQualified(Boolean qualified) {
		this.qualified = qualified;
	}
	

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	@Override
	public String toString() {
		return "FiveStarNetTreeNode [toString()=" + super.toString() + ", snapshotDate=" + snapshotDate + ", ppv=" + ppv
				+ "]";
	}
	
	
	
}
