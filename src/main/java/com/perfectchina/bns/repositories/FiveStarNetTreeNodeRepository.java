package com.perfectchina.bns.repositories;

import com.perfectchina.bns.model.treenode.FiveStarNetTreeNode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FiveStarNetTreeNodeRepository extends TreeNodeRepository<FiveStarNetTreeNode> {
	@Modifying
	@Query("DELETE FROM FiveStarNetTreeNode WHERE pin = 'MEMBER'")
	void deleteLevel();

}