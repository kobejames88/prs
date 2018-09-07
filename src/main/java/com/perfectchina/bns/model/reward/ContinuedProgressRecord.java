package com.perfectchina.bns.model.reward;

import com.perfectchina.bns.model.Account;

import javax.persistence.*;

/**
 * @Author: chenhuahai
 * @Date: 2018/9/5
 * @Desc: 持续进步得分记录
 */
@Entity
@NamedQuery(name="ContinuedProgressRecord.findAll", query="SELECT b FROM ContinuedProgressRecord b")
public class ContinuedProgressRecord {

    //初始化数据为0
    public ContinuedProgressRecord(int zero) {
        R = 0;
        this.newCountR = 0;
        this.oldCountR = 0;
        E = 0;
        this.newCountE = 0;
        this.oldCountE = 0;
        D = 0;
        this.newCountD = 0;
        this.oldCountD = 0;
        G = 0;
        this.newCountG = 0;
        this.oldCountG = 0;
        this.downlinePoint = 0;
    }
    public ContinuedProgressRecord() {
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name="AccountId")
    private Account account;

    //某会员该月在红宝石职级得分
    private Integer R;
    //新晋连续次数；连续
    private Integer newCountR;
    //新晋满十二个月后，连续不是在该职级次数；连续
    private Integer oldCountR;

    //翡翠
    private Integer E;
    private Integer newCountE;
    private Integer oldCountE;

    //钻石
    private Integer D;
    private Integer newCountD;
    private Integer oldCountD;

    //金钻、双金钻、三金钻
    private Integer G;
    private Integer newCountG;
    private Integer oldCountG;

    //获得下级分
    private Integer downlinePoint;

    //总得分
    @Transient
    private Integer total;

    //数据库中不存在该字段，需要值就计算
    public Integer getTotal() {
        return this.R+this.E+this.D+this.G+this.downlinePoint;
    }

    //时间yyyyMM
    private String snapshotDate;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Integer getR() {
        return R;
    }

    public void setR(Integer r) {
        R = r;
    }

    public Integer getNewCountR() {
        return newCountR;
    }

    public void setNewCountR(Integer newCountR) {
        this.newCountR = newCountR;
    }

    public Integer getOldCountR() {
        return oldCountR;
    }

    public void setOldCountR(Integer oldCountR) {
        this.oldCountR = oldCountR;
    }

    public Integer getE() {
        return E;
    }

    public void setE(Integer e) {
        E = e;
    }

    public Integer getNewCountE() {
        return newCountE;
    }

    public void setNewCountE(Integer newCountE) {
        this.newCountE = newCountE;
    }

    public Integer getOldCountE() {
        return oldCountE;
    }

    public void setOldCountE(Integer oldCountE) {
        this.oldCountE = oldCountE;
    }

    public Integer getD() {
        return D;
    }

    public void setD(Integer d) {
        D = d;
    }

    public Integer getNewCountD() {
        return newCountD;
    }

    public void setNewCountD(Integer newCountD) {
        this.newCountD = newCountD;
    }

    public Integer getOldCountD() {
        return oldCountD;
    }

    public void setOldCountD(Integer oldCountD) {
        this.oldCountD = oldCountD;
    }

    public Integer getG() {
        return G;
    }

    public void setG(Integer g) {
        G = g;
    }

    public Integer getNewCountG() {
        return newCountG;
    }

    public void setNewCountG(Integer newCountG) {
        this.newCountG = newCountG;
    }

    public Integer getOldCountG() {
        return oldCountG;
    }

    public void setOldCountG(Integer oldCountG) {
        this.oldCountG = oldCountG;
    }

    public String getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(String snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getDownlinePoint() {
        return downlinePoint;
    }

    public void setDownlinePoint(Integer downlinePoint) {
        this.downlinePoint = downlinePoint;
    }
}
