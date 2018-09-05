package com.perfectchina.bns.repositories;

import com.perfectchina.bns.model.treenode.OpvNetTreeNode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpvNetTreeNodeRepository extends TreeNodeRepository<OpvNetTreeNode> {
    @Query("SELECT sr FROM OpvNetTreeNode sr WHERE sr.uplinkId = 0 and sr.snapshotDate BETWEEN :fromDate AND :toDate order by sr.snapshotDate")
    public List<OpvNetTreeNode> findBySnapshotDate(
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate);
}