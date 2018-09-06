package com.perfectchina.bns.repositories;

import com.perfectchina.bns.model.treenode.ActiveNetTreeNode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ActiveNetTreeNodeRepository extends TreeNodeRepository<ActiveNetTreeNode> {

	@Modifying
	@Transactional
	@Query("DELETE FROM ActiveNetTreeNode WHERE pv < 200 and levelNum > 0")
	void deleteNOActiveMember();

}