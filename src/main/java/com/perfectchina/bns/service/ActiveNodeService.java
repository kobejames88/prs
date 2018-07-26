package com.perfectchina.bns.service;

import java.util.Date;

public interface ActiveNodeService extends TreeNodeService{
	void setPreviousDateEndTime(Date previousDateEndTime);
	void createActiveNetTree();
}
