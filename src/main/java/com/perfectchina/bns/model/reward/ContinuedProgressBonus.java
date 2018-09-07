package com.perfectchina.bns.model.reward;

import com.perfectchina.bns.model.Account;

import javax.persistence.*;

/**
 * @Author: chenhuahai
 * @Date: 2018/9/5
 * @Desc: 持续进步奖；季度奖
 */
@Entity
@NamedQuery(name="ContinuedProgressBonus.findAll", query="SELECT b FROM ContinuedProgressBonus b")
public class ContinuedProgressBonus {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name="AccountId")
    private Account account;

    //季度奖金
    private float bonus;

    //季度总分
    private int totalPoint;

    //季度时间yyyy-MM-MM-MM
    private String snapshotDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public float getBonus() {
        return bonus;
    }

    public void setBonus(float bonus) {
        this.bonus = bonus;
    }

    public String getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(String snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public int getTotalPoint() {
        return totalPoint;
    }

    public void setTotalPoint(int totalPoint) {
        this.totalPoint = totalPoint;
    }
}
