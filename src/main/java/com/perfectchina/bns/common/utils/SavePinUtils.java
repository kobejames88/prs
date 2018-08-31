package com.perfectchina.bns.common.utils;

import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.AccountPinHistory;
import com.perfectchina.bns.repositories.AccountPinHistoryRepository;
import com.perfectchina.bns.repositories.AccountRepository;
import com.perfectchina.bns.service.Enum.Pin;
import com.perfectchina.bns.service.pin.PinPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class SavePinUtils {
    public static void savePinAndHistory(Account account, String pin,AccountPinHistoryRepository accountPinHistoryRepository,AccountRepository accountRepository){
        account.setPin(pin);
        String maxPin = account.getMaxPin();
        Integer max = Pin.descOf(pin).getCode();
        Integer temp = Pin.descOf(maxPin).getCode();
        if (max > temp){
            account.setMaxPin(pin);
            Integer f_star = Pin.descOf(PinPosition.FIVE_STAR).getCode();
            if (max == Pin.descOf(PinPosition.FIVE_STAR).getCode()) temp = f_star-1;
            while (max > temp){
                temp+=1;
                String temp_pin = Pin.codeOf(temp).getDesc();
                AccountPinHistory accountPinHistory = new AccountPinHistory();
                accountPinHistory.setPromotionDate(new Date());
                accountPinHistory.setAccount(account);
                accountPinHistory.setCreatedBy("TerryTang");
                accountPinHistory.setLastUpdatedBy("TerryTang");
                accountPinHistory.setPin(temp_pin);
                accountPinHistoryRepository.save(accountPinHistory);
            }
        }
        accountRepository.save(account);
    }
}
