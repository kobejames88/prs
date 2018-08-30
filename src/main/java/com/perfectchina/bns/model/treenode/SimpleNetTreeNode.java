package com.perfectchina.bns.model.treenode;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

/**
 * The persistent class for the simple tree node database table. It is pre-process for other  
 * Network node creation
 * 
 */
@Entity
@Table(name = "SimpleNetTreeNode")
@NamedQuery(name="SimpleNetTreeNode.findAll", query="SELECT a FROM SimpleNetTreeNode a")
public class SimpleNetTreeNode extends TreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Float ppv;

	private Float totalSales;

    public Float getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Float totalSales) {
        this.totalSales = totalSales;
    }

    public Float getPpv() {
		return ppv;
	}

	public void setPpv(Float ppv) {
		this.ppv = ppv;
	}

	@Override
	public String toString() {
		return "SimpleNetTreeNode [ppv=" + ppv + ", toString()=" + super.toString() + "]";
	}


	
}
