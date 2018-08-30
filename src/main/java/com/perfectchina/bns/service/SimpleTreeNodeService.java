package com.perfectchina.bns.service;

import com.perfectchina.bns.model.vo.SimpleVo;

import java.util.Date;
import java.util.List;

public interface SimpleTreeNodeService extends TreeNodeService{
	void setPreviousDateEndTime(Date previousDateEndTime);
	List<SimpleVo> convertSimpleVo(String snapshotDate);
}
