package com.perfectchina.bns.service.pin;

import java.util.Date;

public class CheckPinResult {

	private String oldPin;
	private String newPin;
	
	private boolean pinStatusChanged;
	
	private Date qualifiedFrom;
	private Date qualifiedTo;
	public String getOldPin() {
		return oldPin;
	}
	public void setOldPin(String oldPin) {
		this.oldPin = oldPin;
	}
	public String getNewPin() {
		return newPin;
	}
	public void setNewPin(String newPin) {
		this.newPin = newPin;
	}
	public boolean isPinStatusChanged() {
		return pinStatusChanged;
	}
	public void setPinStatusChanged(boolean pinStatusChanged) {
		this.pinStatusChanged = pinStatusChanged;
	}
	public Date getQualifiedFrom() {
		return qualifiedFrom;
	}
	public void setQualifiedFrom(Date qualifiedFrom) {
		this.qualifiedFrom = qualifiedFrom;
	}
	public Date getQualifiedTo() {
		return qualifiedTo;
	}
	public void setQualifiedTo(Date qualifiedTo) {
		this.qualifiedTo = qualifiedTo;
	}
	@Override
	public String toString() {
		return "CheckPinResult [oldPin=" + oldPin + ", newPin=" + newPin
				+ ", pinStatusChanged=" + pinStatusChanged + ", qualifiedFrom="
				+ qualifiedFrom + ", qualifiedTo=" + qualifiedTo + "]";
	}
	
	
	
	
}
