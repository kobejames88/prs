package com.perfectchina.bns.service;

import java.util.Date;

public interface GpvTreeNodeService extends TreeNodeService{
	void setPreviousDateEndTime(Date previousDateEndTime);
	void updateWholeTreeGPV();
}
