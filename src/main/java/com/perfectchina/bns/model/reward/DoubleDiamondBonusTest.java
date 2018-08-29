package com.perfectchina.bns.model.reward;

import com.perfectchina.bns.model.Account;

import javax.persistence.*;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/29
 * @Desc: 双金钻奖模拟测试数据
 */
@Entity
@NamedQuery(name="DoubleDiamondBonusTest.findAll", query="SELECT a FROM DoubleDiamondBonusTest a")
public class DoubleDiamondBonusTest {


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    //bi-directional many-to-one association to Account
    @ManyToOne
    @JoinColumn(name="AccountId")
    private Account account;

    private  String snapshotDate;

    private  float bonus;

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

    public String getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(String snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public float getBonus() {
        return bonus;
    }

    public void setBonus(float bonus) {
        this.bonus = bonus;
    }
}
