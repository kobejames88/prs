package com.perfectchina.bns.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.perfectchina.bns.model.CustomerBonusRate;
import com.perfectchina.bns.repositories.CustomerBonusRateRepository;

@Transactional
@Service
public class CustomerBonusRateServiceImpl implements CustomerBonusRateService {

	@Autowired
	private CustomerBonusRateRepository customerBonusRateRepository;

	@Override
	public CustomerBonusRate getBonusRateByAopvDesc(float aopv, Date checkAsAtDate) {
		List<CustomerBonusRate> customerBonusRates = customerBonusRateRepository.findBonusRateByAopvAndDateDesc(aopv, checkAsAtDate);
		CustomerBonusRate result = null;
		for ( CustomerBonusRate customerBonusRate: customerBonusRates ) {
			result = customerBonusRate;
			break;
		}

		return result;
		
	}



}