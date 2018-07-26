package com.perfectchina.bns.service;

import java.util.Date;

public interface OpvTreeNodeService extends TreeNodeService{
	void setPreviousDateEndTime(Date previousDateEndTime);

	void updateWholeTreeOPV();
}
