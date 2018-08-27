package com.perfectchina.bns.service.reward;

import com.perfectchina.bns.model.CustomerBonusRate;
import com.perfectchina.bns.model.reward.GoldenDiamondOPVBonusRate;
import com.perfectchina.bns.repositories.reward.GoldenDiamondOPVBonusRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author: chenhuahai
 * @Date: 2018/8/27
 * @Desc:
 */
@Service
public class GoldenDiamondOPVBonusRateServiceImpl implements  GoldenDiamondOPVBonusRateService {

    @Autowired
    GoldenDiamondOPVBonusRateRepository goldenDiamondOPVBonusRateRepository;

    @Override
    public GoldenDiamondOPVBonusRate findBonusRateByDate(Date checkAsAtDate) {
        List<GoldenDiamondOPVBonusRate> bonusRates = goldenDiamondOPVBonusRateRepository.findBonusRateByDate(checkAsAtDate);
        GoldenDiamondOPVBonusRate goldenDiamondOPVBonusRate = null;
       if(bonusRates!=null&&bonusRates.size()>0){
           goldenDiamondOPVBonusRate = bonusRates.get(0);
       }
        return goldenDiamondOPVBonusRate;
    }
}
