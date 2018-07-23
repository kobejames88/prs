package com.perfectchina.bns.model;

public interface InterfaceInfoStatus {
	public final static String PENDING = "Pending";
	public final static String CONFIRMED = "Confirmed";
	public final static String IMPORTED = "Imported";
	public final static String SKIPPED = "Skipped";
	
	
	public final static String STATUS_ACTIVE = "A";
	public final static String STATUS_DELETED = "D";
	public final static String STATUS_SUSPENDED = "S";
	public final static String STATUS_PURGED = "P";
	public final static String STATUS_PLACE_HOLDER = "H";

	
	public final static String ACTION_ADD = "ADD";
	public final static String ACTION_REMOVE = "REMOVE";
	public final static String ACTION_MODIFY = "MODIFY";
	
}
