package com.perfectchina.bns.repositories;

import com.perfectchina.bns.model.treenode.QualifiedFiveStarNetTreeNode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QualifiedFiveStarNetTreeNodeRepository extends TreeNodeRepository<QualifiedFiveStarNetTreeNode> {
    @Query("SELECT count(a) FROM QualifiedFiveStarNetTreeNode a WHERE a.snapshotDate = :snapshotDate and a.data.pin = :pin")
    public int countTreeNodesBySnapshotDateAndPin(@Param("snapshotDate") String snapshotDate,@Param("pin") String pin);
}