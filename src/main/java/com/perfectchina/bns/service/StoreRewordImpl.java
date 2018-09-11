package com.perfectchina.bns.service;


import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.common.utils.MathUtils;
import com.perfectchina.bns.model.MemberLog;
import com.perfectchina.bns.model.SalesRecord;
import com.perfectchina.bns.model.reward.StoreReward;
import com.perfectchina.bns.model.reward.StoreRewardsRate;
import com.perfectchina.bns.repositories.MemberLogRepositories;
import com.perfectchina.bns.repositories.SalesRecordRepository;
import com.perfectchina.bns.repositories.StoreRewardRepository;
import com.perfectchina.bns.repositories.StoreRewardsRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * creat by xb
 */
@Service
public class StoreRewordImpl implements StoreRewordService {

    private final Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Autowired
    private StoreRewardsRateRepository storeRewardsRepository;

    @Autowired
    private MemberLogRepositories serviceCenterRepositories;

    @Autowired
    private SalesRecordRepository salesRecordRepository;

    @Autowired
    private StoreRewardRepository storeRewardRepository;


    @Override
    public List<StoreReward> findInfo(String date) {
        calculateBonus(date);
        List<StoreReward> storeRewards= storeRewardRepository.findInfo(date);
        return storeRewards;
    }

    //计算奖金
    @Override
    public void  calculateBonus(String date) {
        float bonus=0F;
        float total=0F;
        List<SalesRecord> salesRecords=salesRecordRepository.selectServiceNum(date);

        if(salesRecords.size()==0){
            StoreReward storeReward=new StoreReward();
            logger.error("该用户没有进行服务中心报单");
            bonus= 0;
            storeReward.setReword(bonus);
            storeReward.setRewardDate(DateUtils.convertSubmissionDate(date));
            storeReward.setServiceCenterNum(null);
            storeRewardRepository.saveAndFlush(storeReward);
        }
        for(int i=0;i<salesRecords.size();i++){
            StoreReward storeReward=new StoreReward();
            String centerNum= salesRecords.get(i).getServiceCenterNum();
            List<SalesRecord> salesRecordList =salesRecordRepository.getByAccountNum(centerNum,date);
               for(SalesRecord salesRecord1:salesRecordList){
                 total+=salesRecord1.getSalesPV();
               }
            StoreRewardsRate storeRewards=storeRewardsRepository.findData();
            Date submissionDate=DateUtils.convertSubmissionDate(date);

                MemberLog memberLog=serviceCenterRepositories.findDate(centerNum);
                storeReward.setRewardDate(submissionDate);
                storeReward.setServiceCenterNum(centerNum);
                StoreReward storeReward1=storeRewardRepository.findId(centerNum,date);
                if(storeReward1!=null){
                    storeReward.setId(storeReward1.getId());
                }
                boolean exist=isEffectiveDate(submissionDate,storeRewards.getStartTime(),storeRewards.getEndTime());
                //违规，不在时间段内 0.05
                if(memberLog!=null&&memberLog.getViolation()==true&&exist==false){
                    bonus=total*storeRewards.getBasicRate();
                    storeReward.setReword(MathUtils.round(bonus));
                    storeRewardRepository.saveAndFlush(storeReward);
                }
                else if(memberLog==null||memberLog.getViolation()==false&&exist==false){
                    //不违规，不在时间段0.06
                    bonus=total*(storeRewards.getBasicRate()+storeRewards.getViolationRate());
                    storeReward.setReword(MathUtils.round(bonus));
                    storeRewardRepository.saveAndFlush(storeReward);
                }
                else if(memberLog!=null&&memberLog.getViolation()==true&&exist==true){
                    //违规在时间段 0.07
                    bonus=total*(storeRewards.getBasicRate()+storeRewards.getSupportRate());
                    storeReward.setReword(MathUtils.round(bonus));
                    storeRewardRepository.saveAndFlush(storeReward);
                }
                else if(memberLog==null||memberLog.getViolation()==false&&exist==true){
                    //不违规，在同时间 0.08
                    bonus=total*(storeRewards.getBasicRate()+storeRewards.getSupportRate()+storeRewards.getViolationRate());
                    storeReward.setReword(MathUtils.round(bonus));
                    storeRewardRepository.saveAndFlush(storeReward);
                }else {
                    bonus=  0F;
                }
            total=0F;

        }


    }


    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     * @param submissionDate 当前时间
     * @param startTime 开始时间 数据库查出来
     * @param endTime 结束时间   查出来
     */
    public static boolean isEffectiveDate(Date submissionDate, Date startTime, Date endTime) {

        if (submissionDate.getTime() == startTime.getTime()
                || submissionDate.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(submissionDate);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }
}
