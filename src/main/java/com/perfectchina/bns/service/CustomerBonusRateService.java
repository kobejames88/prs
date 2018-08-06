package com.perfectchina.bns.service;

import java.util.Date;

import com.perfectchina.bns.model.CustomerBonusRate;

public interface CustomerBonusRateService {

	public CustomerBonusRate getBonusRateByAopvDesc( float aopv, Date checkAsAtDate );	
}
