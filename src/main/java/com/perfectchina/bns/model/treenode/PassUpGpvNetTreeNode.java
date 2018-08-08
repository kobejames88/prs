package com.perfectchina.bns.model.treenode;

import javax.persistence.Column;
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

    @Column(columnDefinition="float default 0.00")
    private Float gpv;
	// PassUpGpv
    @Column(columnDefinition="float default 0.00")
	private Float passUpGpv;
	// Lower level is a qualified five star or more
    @Column(columnDefinition="int default 0")
	private int qualifiedLine;

    private Boolean hasAsteriskNode;

    @Column(columnDefinition="float default 0.00")
    private Float asteriskNodePoints;

    public Boolean getHasAsteriskNode() {
        return hasAsteriskNode;
    }

    public void setHasAsteriskNode(Boolean hasAsteriskNode) {
        this.hasAsteriskNode = hasAsteriskNode;
    }

    public Float getAsteriskNodePoints() {
        return asteriskNodePoints;
    }

    public void setAsteriskNodePoints(Float asteriskNodePoints) {
        this.asteriskNodePoints = asteriskNodePoints;
    }

    public Float getGpv() {
        return gpv;
    }

    public void setGpv(Float gpv) {
        this.gpv = gpv;
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

	@Override
	public String toString() {
		return "OpvNetTreeNode [passUpGpv=" + passUpGpv + ", qualifiedLine=" + qualifiedLine + ", toString()=" + super.toString() + "]";
	}


	
}
