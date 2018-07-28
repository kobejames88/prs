package com.perfectchina.bns.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.perfectchina.bns.model.treenode.FiveStarNetTreeNode;

@Repository
public interface FiveStarNetTreeNodeRepository extends TreeNodeRepository<FiveStarNetTreeNode> {
	@Modifying
	@Query("DELETE FROM FiveStarNetTreeNode WHERE pin = 'MEMBER'")
	void deleteLevel();

}