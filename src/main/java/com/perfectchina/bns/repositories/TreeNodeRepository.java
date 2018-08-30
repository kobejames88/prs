package com.perfectchina.bns.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.treenode.SimpleNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;

@Repository
public interface TreeNodeRepository<T extends TreeNode> extends JpaRepository<T, Long> {
// public interface TreeNodeRepository extends JpaRepository<TreeNode, Long> {

	@Query("SELECT a FROM #{#entityName} a WHERE a.uplinkId = :parentId")
	public List<T> findByParentId(@Param("parentId") Long parentId);
	
	@Query("SELECT a FROM #{#entityName} a WHERE a.uplinkId = :parentId AND a.data.status = 'A' order by a.id")
	public List<T> findChildAccoutsByParentId(
			@Param("parentId") Long parentId);
	
	@Query("SELECT a FROM #{#entityName} a WHERE a.data.accountNum = :accountNum order by a.id desc")
	public T getAccountByAccountNum(@Param("accountNum") String accountNum);

	@Query("SELECT a FROM #{#entityName} a WHERE a.snapshotDate = :snapShotDate and a.data.accountNum = :accountNum order by a.id desc")
	public T getAccountByAccountNum(@Param("snapShotDate") String snapShotDate, @Param("accountNum") String accountNum);
	
	@Query("SELECT a FROM #{#entityName} a WHERE a.data.accountNum = :accountNum order by a.id desc")
	public List<T> findByAccountNum(@Param("accountNum") String accountNum);

	@Query("SELECT a FROM #{#entityName} a WHERE a.snapshotDate = :snapShotDate and a.data.accountNum = :accountNum order by a.id desc")
	public T findByAccountNum(@Param("snapShotDate") String snapShotDate,@Param("accountNum") String accountNum);
	
	// need to update the field hasChild to true if the account actually has distributor child (Not VIP Child)
	//@Query("SELECT a FROM Account a WHERE a.hasChild = FALSE AND a.id IN (SELECT DISTINCT a2.uplinkId FROM Account a2 WHERE a2.pin in ('T','AS','S','M','SM','EM','D','SD') )")
	@Query("SELECT a FROM #{#entityName} a WHERE a.hasChild = FALSE AND a.id IN (SELECT DISTINCT a2.uplinkId FROM #{#entityName} a2 )")
	public List<T> retrieveInCorrectHasChildAccounts();
	
	@Query("SELECT a FROM #{#entityName} a WHERE a.hasChild = FALSE and a.snapshotDate = :snapshotDate ")
	public List<T> findLeafNodes(@Param("snapshotDate") String snapshotDate);
	
	// need to find out the account has no distributor child 
	//@Query("SELECT a FROM Account a WHERE a.hasChild = TRUE AND a.id NOT IN (SELECT DISTINCT a2.uplinkId FROM Account a2 WHERE a2.pin in ('T','AS','S','M','SM','EM','D','SD') )")
	@Query("SELECT a FROM #{#entityName} a WHERE a.hasChild = TRUE AND a.id NOT IN (SELECT DISTINCT a2.uplinkId FROM #{#entityName} a2 )")
	public List<T> retrieveInCorrectLeafAccounts();	
	
	@Query("SELECT a FROM #{#entityName} a WHERE a.snapshotDate = :snapshotDate and a.levelNum = 0 order by a.id")
	public T getRootTreeNode(@Param("snapshotDate") String snapshotDate);

	@Query("SELECT max(a.levelNum) FROM #{#entityName} a WHERE a.snapshotDate = :snapshotDate")
	public Integer getMaxLevelNum(@Param("snapshotDate") String snapshotDate);

	@Query("SELECT a FROM #{#entityName} a WHERE a.levelNum = 0 and a.snapshotDate = :snapshotDate order by a.id")
	public T getRootTreeNodeOfMonth(@Param("snapshotDate") String snapshotDate);
	
	@Query("SELECT a FROM #{#entityName} a WHERE a.levelNum = :levelNum order by a.id")
	public List<T> getTreeNodesByLevel(@Param("levelNum")Integer levelNum);

	@Query("SELECT a FROM #{#entityName} a WHERE a.snapshotDate = :snapshotDate and a.levelNum = :levelNum order by a.id")
	public List<T> getTreeNodesByLevelAndSnapshotDate(@Param("snapshotDate") String snapshotDate,@Param("levelNum")Integer levelNum);

	@Query("SELECT a FROM #{#entityName} a WHERE a.snapshotDate = :snapshotDate order by a.levelNum")
	public List<T> getTreeNodesBySnapshotDate(@Param("snapshotDate") String snapshotDate);
 
	@Query("SELECT a FROM #{#entityName} a WHERE a.uplinkId = :uplinkId order by a.id")
	public List<T> getChildNodesByUpid(@Param("uplinkId")long uplinkId);

	@Query("SELECT count(a) FROM #{#entityName} a WHERE a.uplinkId = :uplinkId")
	public Integer findChildNodesNumberByUpid(@Param("uplinkId")long uplinkId);
	
}