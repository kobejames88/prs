package com.perfectchina.bns.service;

import java.util.Date;

public interface QualifiedFiveStarTreeNodeService extends TreeNodeService{
	void setPreviousDateEndTime(Date previousDateEndTime);
	void updateWholeTreeQualifiedFiveStar();
}
