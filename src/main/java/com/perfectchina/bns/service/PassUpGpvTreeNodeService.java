package com.perfectchina.bns.service;

import java.util.Date;

public interface PassUpGpvTreeNodeService extends TreeNodeService{
	void setPreviousDateEndTime(Date previousDateEndTime);
	void updateWholeTreePassUpGPV(String snapShotDate);
}
