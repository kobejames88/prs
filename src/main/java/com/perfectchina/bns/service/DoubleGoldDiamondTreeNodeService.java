package com.perfectchina.bns.service;

import com.perfectchina.bns.model.vo.DoubleGoldDiamonndVo;
import com.perfectchina.bns.model.vo.GoldDiamonndVo;

import java.util.Date;
import java.util.List;

public interface DoubleGoldDiamondTreeNodeService extends TreeNodeService{
	void setPreviousDateEndTime(Date previousDateEndTime);
	void updateWholeTreeDoubleGoldDiamond(String snapshotDate);
	List<DoubleGoldDiamonndVo> convertDoubleGoldDiamondVo(String snapshotDate);
}
