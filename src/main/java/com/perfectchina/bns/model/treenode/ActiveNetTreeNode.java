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
@Table(name = "ActiveNetTreeNode")
@NamedQuery(name="ActiveNetTreeNode.findAll", query="SELECT a FROM ActiveNetTreeNode a")
public class ActiveNetTreeNode extends TreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String snapshotDate; // yyyyMM
	
	private float pv;
	
	private float gpv;
	
	private float opv;
	
	private Boolean isActiveMember;

	public String getSnapshotDate() {
		return snapshotDate;
	}

	public void setSnapshotDate(String snapshotDate) {
		this.snapshotDate = snapshotDate;
	}

	public float getPv() {
		return pv;
	}

	public void setPv(float pv) {
		this.pv = pv;
	}

	public float getGpv() {
		return gpv;
	}

	public void setGpv(float gpv) {
		this.gpv = gpv;
	}

	public float getOpv() {
		return opv;
	}

	public void setOpv(float opv) {
		this.opv = opv;
	}

	public Boolean getIsActiveMember() {
		return isActiveMember;
	}

	public void setIsActiveMember(Boolean isActiveMember) {
		this.isActiveMember = isActiveMember;
	}

	@Override
	public String toString() {
		return "SimpleNetTreeNode [toString()=" + super.toString() + ", snapshotDate=" + snapshotDate + ", pv=" + pv
				+ ", gpv=" + gpv + ", opv=" + opv + "]";
	}
	
	
	
}
