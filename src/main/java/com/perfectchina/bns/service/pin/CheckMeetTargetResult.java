package com.perfectchina.bns.service.pin;

import java.util.Date;

public class CheckMeetTargetResult {

	private boolean isMeetQualifiedFiveStar;
	private Date lastDateMeetQualifiedFiveStar;

	
	public boolean isMeetQualifiedFiveStar() {
		return isMeetQualifiedFiveStar;
	}


	public void setMeetQualifiedFiveStar(boolean isMeetQualifiedFiveStar) {
		this.isMeetQualifiedFiveStar = isMeetQualifiedFiveStar;
	}


	public Date getLastDateMeetQualifiedFiveStar() {
		return lastDateMeetQualifiedFiveStar;
	}


	public void setLastDateMeetQualifiedFiveStar(Date lastDateMeetQualifiedFiveStar) {
		this.lastDateMeetQualifiedFiveStar = lastDateMeetQualifiedFiveStar;
	}


	@Override
	public String toString() {
		return "CheckMeetTargetResult [isMeetQualifiedFiveStar=" + isMeetQualifiedFiveStar
				+ ", lastDateMeetQualifiedFiveStar=" + lastDateMeetQualifiedFiveStar + "]";
	}

	
	
	
	
}
