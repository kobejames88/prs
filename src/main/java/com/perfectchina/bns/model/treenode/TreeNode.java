package com.perfectchina.bns.model.treenode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.perfectchina.bns.model.Account;

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class TreeNode implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	private String snapshotDate; // yyyyMM
		
	private long uplinkId;

	private Integer levelNum;
	
	private Boolean hasChild;

	// separate structure and business data
	@OneToOne
	private Account data;

	// Connect according to uplinkId
	@OneToMany(mappedBy = "uplinkId")
	@JsonIgnore
	List<TreeNode> childNodes = new ArrayList<>(0);
	public List<TreeNode> getChildNodes() {
		return childNodes;
	}
	
	
	
	public String getSnapshotDate() {
		return snapshotDate;
	}



	public void setSnapshotDate(String snapshotDate) {
		this.snapshotDate = snapshotDate;
	}



	public Integer getLevelNum() {
		return this.levelNum;
	}

	public void setLevelNum(Integer levelNum) {
		this.levelNum = levelNum;
	}

	public Boolean getHasChild() {
		return this.hasChild;
	}

	public void setHasChild(Boolean hasChild) {
		this.hasChild = hasChild;
	}

	public long getUplinkId() {
		return uplinkId;
	}

	public void setUplinkId(long uplinkId) {
		this.uplinkId = uplinkId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Account getData() {
		return data;
	}

	public void setData(Account data) {
		this.data = data;
	}

	public void setChildNodes(List<TreeNode> childNodes) {
		this.childNodes = childNodes;
	}



	@Override
	public String toString() {
		return "TreeNode [id=" + id + ", snapshotDate=" + snapshotDate + ", uplinkId=" + uplinkId + ", levelNum="
				+ levelNum + ", hasChild=" + hasChild + ", data=" + data + ", childNodes=" + childNodes + "]";
	}

	
}
