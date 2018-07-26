package com.perfectchina.bns.model.treenode;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * PassUpGpv
 */
@Entity
@Table(name = "PassUpGpvNetTreeNode")
@NamedQuery(name="PassUpGpvNetTreeNode.findAll", query="SELECT a FROM PassUpGpvNetTreeNode a")
public class PassUpGpvNetTreeNode extends TreeNode {
	private static final long serialVersionUID = 1L;

	// PassUpGpv
	private Float passUpGpv;
	// Individuals and subordinates do not reach the five-star total PPV sum
	private Float gpv;
	// Lower level is a qualified five star or more
	private int qualifiedLine;

	private Boolean isActiveMember;

	public Boolean getIsActiveMember() {
		return isActiveMember;
	}

	public void setIsActiveMember(Boolean isActiveMember) {
		this.isActiveMember = isActiveMember;
	}

	public Float getPassUpGpv() {
		return passUpGpv;
	}

	public void setPassUpGpv(Float passUpGpv) {
		this.passUpGpv = passUpGpv;
	}

	public int getQualifiedLine() {
		return qualifiedLine;
	}

	public void setQualifiedLine(int qualifiedLine) {
		this.qualifiedLine = qualifiedLine;
	}

	public Float getGpv() {
		return gpv;
	}

	public void setGpv(Float gpv) {
		this.gpv = gpv;
	}

	@Override
	public String toString() {
		return "OpvNetTreeNode [passUpGpv=" + passUpGpv + ", gpv=" + gpv + ", qualifiedLine=" + qualifiedLine + ", toString()=" + super.toString() + "]";
	}


	
}
