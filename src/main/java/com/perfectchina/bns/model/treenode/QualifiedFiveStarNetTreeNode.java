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

//    private Boolean isEndFiveStar;

    private Float ppv;

    private Float gpv;

    private Float opv;

    private Float fiveStarIntegral;

    private Float borrowPoints;

	private Float borrowedPoints;

	private Long borrowTo;

    private Boolean hasAsteriskNode;

    private Boolean AboveEmeraldNodeSign;

    private float asteriskNodePoints;

    private int emeraldLine;

    private int goldDiamondLine;

    private String levelLine;

    private String pin;

//    public Boolean getEndFiveStar() {
//        return isEndFiveStar;
//    }
//
//    public void setEndFiveStar(Boolean endFiveStar) {
//        isEndFiveStar = endFiveStar;
//    }

    public Float getPpv() {
        return ppv;
    }

    public void setPpv(Float ppv) {
        this.ppv = ppv;
    }

    public Boolean getAboveEmeraldNodeSign() {
        return AboveEmeraldNodeSign;
    }

    public void setAboveEmeraldNodeSign(Boolean aboveEmeraldNodeSign) {
        AboveEmeraldNodeSign = aboveEmeraldNodeSign;
    }

    public Float getOpv() {
        return opv;
    }

    public void setOpv(Float opv) {
        this.opv = opv;
    }

    public void setAsteriskNodePoints(float asteriskNodePoints) {
        this.asteriskNodePoints = asteriskNodePoints;
    }

    public int getEmeraldLine() {
        return emeraldLine;
    }

    public void setEmeraldLine(int emeraldLine) {
        this.emeraldLine = emeraldLine;
    }

    public int getGoldDiamondLine() {
        return goldDiamondLine;
    }

    public void setGoldDiamondLine(int goldDiamondLine) {
        this.goldDiamondLine = goldDiamondLine;
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
