package com.perfectchina.bns.model.reward;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: TerryTang
 * @Date: 2018/9/11 上午11:27
 * @Desc:
 */
@Entity
@NamedQuery(name="BottomQualifiedFiveStarReward.findAll", query="SELECT a FROM BottomQualifiedFiveStarReward a")
public class BottomQualifiedFiveStarReward {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private String createdBy;

    @Temporal(TemporalType.DATE)
    private Date creationDate;

    private BigDecimal bottomQualifiedFiveStarReward;

    private String lastUpdatedBy;

    @Temporal(TemporalType.DATE)
    private Date lastUpdatedDate;

    public BottomQualifiedFiveStarReward() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public BigDecimal getBottomQualifiedFiveStarReward() {
        return bottomQualifiedFiveStarReward;
    }

    public void setBottomQualifiedFiveStarReward(BigDecimal bottomQualifiedFiveStarReward) {
        this.bottomQualifiedFiveStarReward = bottomQualifiedFiveStarReward;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    @PreUpdate
    @PrePersist
    public void updateTimeStamps() {
        Date today = new Date();
        lastUpdatedDate = today;
        if (creationDate==null) {
            creationDate = today;
        }
    }
}
