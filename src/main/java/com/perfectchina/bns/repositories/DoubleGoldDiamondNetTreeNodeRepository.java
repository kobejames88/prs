package com.perfectchina.bns.repositories;

import com.perfectchina.bns.model.treenode.DoubleGoldDiamondNetTreeNode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DoubleGoldDiamondNetTreeNodeRepository extends TreeNodeRepository<DoubleGoldDiamondNetTreeNode> {
    @Query("SELECT sum(a.passUpOpv) FROM DoubleGoldDiamondNetTreeNode a WHERE a.snapshotDate = :snapShotDate")
    public Float sumBySnapshotDate(@Param("snapShotDate") String snapShotDate);

    @Query("SELECT a FROM DoubleGoldDiamondNetTreeNode a WHERE a.snapshotDate = :snapShotDate and a.data.id = :AccountId")
    DoubleGoldDiamondNetTreeNode findByAccountId(@Param("snapShotDate") String snapShotDate, @Param("AccountId") Long AccountId);
}