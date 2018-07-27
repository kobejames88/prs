package com.perfectchina.bns.service;

import java.util.Date;

import com.perfectchina.bns.model.CustomerBonusRate;

public interface CustomerBonusRateService {

	public CustomerBonusRate getBonusRateByAopv(float aopv, Date checkAsAtDate);
}
