package com.perfectchina.bns.model.treenode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * QualifiedFiveStar
 */
@Entity
@Table(name = "QualifiedFiveStarNetTreeNode")
@NamedQuery(name="QualifiedFiveStarNetTreeNode.findAll", query="SELECT a FROM QualifiedFiveStarNetTreeNode a")
public class QualifiedFiveStarNetTreeNode extends TreeNode {
	private static final long serialVersionUID = 1L;

    private Float passUpGpv;

    private int qualifiedLine;

    private Float gpv;

    @Column(columnDefinition="float default 0.00")
    private Float fiveStarIntegral;

    @Column(columnDefinition="float default 0.00")
    private Float borrowPoints;

    @Column(columnDefinition="float default 0.00")
	private Float borrowedPoints;

    @Column(columnDefinition="bigint default 0")
	private Long borrowTo;

    private Boolean hasAsteriskNode;

    @Column(columnDefinition="float default 0.00")
    private float asteriskNodePoints;

    @Column(columnDefinition="int default 0")
    private int EmeraldLine;

    @Column(columnDefinition="int default 0")
    private int GoldDiamondLine;

    private String levelLine;

    private String pin;

    public void setAsteriskNodePoints(float asteriskNodePoints) {
        this.asteriskNodePoints = asteriskNodePoints;
    }

    public int getEmeraldLine() {
        return EmeraldLine;
    }

    public void setEmeraldLine(int emeraldLine) {
        EmeraldLine = emeraldLine;
    }

    public int getGoldDiamondLine() {
        return GoldDiamondLine;
    }

    public void setGoldDiamondLine(int goldDiamondLine) {
        GoldDiamondLine = goldDiamondLine;
    }

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

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getLevelLine() {
        return levelLine;
    }

    public void setLevelLine(String levelLine) {
        this.levelLine = levelLine;
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

//    public Boolean getHasAsteriskNode() {
//        return hasAsteriskNode;
//    }
//
//    public void setHasAsteriskNode(Boolean hasAsteriskNode) {
//        this.hasAsteriskNode = hasAsteriskNode;
//    }
//
//    public Float getAsteriskNodePoints() {
//        return asteriskNodePoints;
//    }
//
//    public void setAsteriskNodePoints(Float asteriskNodePoints) {
//        this.asteriskNodePoints = asteriskNodePoints;
//    }

    public Float getBorrowPoints() {
		return borrowPoints;
	}

	public void setBorrowPoints(Float borrowPoints) {
		this.borrowPoints = borrowPoints;
	}

	public Float getBorrowedPoints() {
		return borrowedPoints;
	}

	public void setBorrowedPoints(Float borrowedPoints) {
		this.borrowedPoints = borrowedPoints;
	}

	public Long getBorrowTo() {
		return borrowTo;
	}

	public void setBorrowTo(Long borrowTo) {
		this.borrowTo = borrowTo;
	}

	public Float getFiveStarIntegral() {
		return fiveStarIntegral;
	}

	public void setFiveStarIntegral(Float fiveStarIntegral) {
		this.fiveStarIntegral = fiveStarIntegral;
	}

}
