package com.perfectchina.bns.service;

import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.AccountPinHistory;
import com.perfectchina.bns.repositories.AccountPinHistoryRepository;
import com.perfectchina.bns.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SavePinServiceImpl implements SavePinService{

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountPinHistoryRepository accountPinHistoryRepository;

    @Override
    public void save(String snapShotDate) {
        List<Account> accounts = accountRepository.findAll();
        List<AccountPinHistory> accountPinHistories = new ArrayList<>();
        if (accounts != null){
            for (Account account : accounts){
                AccountPinHistory accountPinHistory = new AccountPinHistory();
                accountPinHistory.setPin(account.getPin());
                accountPinHistory.setCreatedBy("TerryTang");
                accountPinHistory.setLastUpdatedBy("TerryTang");
                accountPinHistory.setAccount(account);
                accountPinHistories.add(accountPinHistory);
            }
        }
        savePin2PinHistory(accountPinHistories);
    }

    //批量存储的集合
    public void savePin2PinHistory(List<AccountPinHistory> accountPinHistories) {
        List<AccountPinHistory> data = new ArrayList<>();
        //批量存储
        for(AccountPinHistory accountPinHistory : accountPinHistories) {
            if(data.size() == 300) {
                accountPinHistoryRepository.saveAll(data);
                data.clear();
            }
            data.add(accountPinHistory);
        }
        if(!data.isEmpty()) {
            accountPinHistoryRepository.saveAll(data);
        }
    }
}
