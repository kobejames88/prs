package com.perfectchina.bns.service;

import com.perfectchina.bns.model.vo.GoldDiamonndVo;

import java.util.Date;
import java.util.List;

public interface GoldDiamondTreeNodeService extends TreeNodeService{
	void setPreviousDateEndTime(Date previousDateEndTime);
	void updateWholeTreeGoldDiamond(String snapshotDate);
    List<GoldDiamonndVo> convertGoldDiamondVo(String snapshotDate);
}
