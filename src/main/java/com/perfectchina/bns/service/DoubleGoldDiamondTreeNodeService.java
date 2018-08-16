package com.perfectchina.bns.service;

import java.util.Date;

public interface DoubleGoldDiamondTreeNodeService extends TreeNodeService{
	void setPreviousDateEndTime(Date previousDateEndTime);
	void updateWholeTreeDoubleGoldDiamond(String snapshotDate);
}
