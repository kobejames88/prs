package com.perfectchina.bns.service;

import java.util.Date;

public interface GoldDiamondTreeNodeService extends TreeNodeService{
	void setPreviousDateEndTime(Date previousDateEndTime);
	void updateWholeTreeGoldDiamond(String snapshotDate);
}
