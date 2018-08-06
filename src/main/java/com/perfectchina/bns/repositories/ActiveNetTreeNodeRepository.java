package com.perfectchina.bns.repositories;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.perfectchina.bns.model.treenode.ActiveNetTreeNode;

@Repository
public interface ActiveNetTreeNodeRepository extends TreeNodeRepository<ActiveNetTreeNode> {

	@Modifying
	@Transactional
	@Query("DELETE FROM ActiveNetTreeNode WHERE isActiveMember = false")
	void deleteNOActiveMember();

}