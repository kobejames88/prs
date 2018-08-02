package com.perfectchina.bns.model;

public class PassUpGpv implements Comparable<PassUpGpv>{
    public Long id;
    public Float passUpGpv;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getPassUpGpv() {
        return passUpGpv;
    }

    public void setPassUpGpv(Float passUpGpv) {
        this.passUpGpv = passUpGpv;
    }

    @Override
    public int compareTo(PassUpGpv o) {
        return this.passUpGpv > o.passUpGpv ? -1 : 1;
    }
}
