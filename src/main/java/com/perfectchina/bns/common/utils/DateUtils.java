package com.perfectchina.bns.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {
	
	private static final String DAY = "D";
	private static final String MONTH = "M";
	private static final String YEAR = "Y";
	
	public static Date getPureToday() {		
		Calendar _cal = Calendar.getInstance();
		_cal.clear(Calendar.HOUR_OF_DAY);
		_cal.clear(Calendar.AM_PM);
		_cal.clear(Calendar.MINUTE);
		_cal.clear(Calendar.SECOND);
		_cal.clear(Calendar.MILLISECOND);
		
		return _cal.getTime();
	}

	/*
	// Use this as the month start count date, do not hardcode the date in the system
	// assume first date is 1
	public static Date getMonthStartDate() {		
		Calendar _cal = Calendar.getInstance();
		_cal.clear(Calendar.HOUR_OF_DAY);
		_cal.clear(Calendar.AM_PM);
		_cal.clear(Calendar.MINUTE);
		_cal.clear(Calendar.SECOND);
		_cal.clear(Calendar.MILLISECOND);
		
		_cal.set( Calendar.DAY_OF_MONTH, 1);
		return _cal.getTime();
	}

	// Use this as the month start count date, do not hardcode the date in the system
	// assume last date of month is 28, 30 or 31
	public static Date getMonthEndDate() {		
		Calendar _cal = Calendar.getInstance();
		_cal.clear(Calendar.HOUR_OF_DAY);
		_cal.clear(Calendar.AM_PM);
		_cal.clear(Calendar.MINUTE);
		_cal.clear(Calendar.SECOND);
		_cal.clear(Calendar.MILLISECOND);

		_cal.set( Calendar.DAY_OF_MONTH, _cal.getActualMaximum( Calendar.DAY_OF_MONTH ));		
		return _cal.getTime();
	}
	*/
	
	// Use this to find out month start date by inputDate, do not hardcode the date in the system
	// Assume the program run at every 8th day of month, and it will calculate back last month data
	public static Date getLastMonthStartDate(Date inputDate) {		
		Calendar cal = Calendar.getInstance();
		cal.setTime( inputDate );
		
		cal.set( Calendar.DAY_OF_MONTH, 1);
		cal.add( Calendar.MONTH, -1);
		
		return cal.getTime();
	}

	// Use this to find out month start date by inputDate, do not hardcode the date in the system
	public static Date getLastMonthEndDate(Date inputDate) {		
		Calendar cal = Calendar.getInstance();
		cal.setTime( inputDate );
		
		// make sure it fall back to previous month
		// prevent case of the input date of 29-31 March 
		cal.set( Calendar.DAY_OF_MONTH, 1);
		cal.add( Calendar.MONTH, -1);
		
		// for the previous month, find the last day
		cal.set( Calendar.DAY_OF_MONTH, cal.getActualMaximum( Calendar.DAY_OF_MONTH ));		
		return cal.getTime();
	}

	public static Date getCurrentDateStartTime(Date inputDate) {		
		Calendar cal = Calendar.getInstance();
		cal.setTime( inputDate );
		
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.AM_PM, Calendar.AM);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		return cal.getTime();
	}

	public static Date getCurrentDateEndTime(Date inputDate) {		
		Calendar cal = Calendar.getInstance();
		cal.setTime( inputDate );
		
		//cal.add( Calendar.DAY_OF_MONTH, -1);
		// cal.set( Calendar.AM_PM, Calendar.PM);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}
	
	public static Date getPreviousDateStartTime(Date inputDate) {		
		Calendar cal = Calendar.getInstance();
		cal.setTime( inputDate );
		
		cal.add( Calendar.DAY_OF_MONTH, -1);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.AM_PM, Calendar.AM);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		return cal.getTime();
	}

	public static Date getPreviousDateEndTime(Date inputDate) {		
		Calendar cal = Calendar.getInstance();
		cal.setTime( inputDate );
		
		cal.add( Calendar.DAY_OF_MONTH, -1);
		// cal.set( Calendar.AM_PM, Calendar.PM);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}
	
	public static Date getMonthStartDate(Date inputDate) {		
		Calendar cal = Calendar.getInstance();
		cal.setTime( inputDate );
		
		cal.set( Calendar.DAY_OF_MONTH, 1);		
		return cal.getTime();
	}
	
	public static Date getMonthEndDate(Date inputDate) {		
		Calendar cal = Calendar.getInstance();
		cal.setTime( inputDate );
		
		// for the previous month, find the last day
		cal.set( Calendar.DAY_OF_MONTH, cal.getActualMaximum( Calendar.DAY_OF_MONTH ));		
		return cal.getTime();
	}
	
	// Use this to find out month start date by inputDate, do not hardcode the date in the system
	// As the job will be run on next month, we need to minus one more month to calculate 12 months of records 
	public static Date getLastYearStartDate(Date inputDate) {		
		Calendar _cal = Calendar.getInstance();
		_cal.setTime( inputDate );

		int previousYear = _cal.get(Calendar.YEAR) - 1;
		_cal.add( Calendar.MONTH, -1);
		_cal.set( Calendar.DAY_OF_MONTH, 1);  
		_cal.set( Calendar.YEAR, previousYear);		
		
		return _cal.getTime();
	}

	
	// Use this to find out month start date by inputDate, do not hardcode the date in the system
	public static Date getLastFiscalYearStartDate(Date inputDate) {		
		Calendar _cal = Calendar.getInstance();
		_cal.setTime( inputDate );
		
		_cal.clear(Calendar.HOUR_OF_DAY);
		_cal.clear(Calendar.AM_PM);
		_cal.clear(Calendar.MINUTE);
		_cal.clear(Calendar.SECOND);
		_cal.clear(Calendar.MILLISECOND);

		int previousYear = _cal.get(Calendar.YEAR) - 1;
		
		
		_cal.set( Calendar.YEAR, previousYear);
		// Month 0 is January, 3 is April
		_cal.set( Calendar.MONTH, 3 ); 		
		_cal.set( Calendar.DAY_OF_MONTH, 1);		
		return _cal.getTime();
	}

	/*
	// Use this to find out month start date by inputDate, do not hardcode the date in the system
	// Assume it will be called only after 31 March of every year.
	public static Date getLastFiscalYearEndDate(Date inputDate) {		
		Calendar _cal = Calendar.getInstance();
		_cal.setTime( inputDate );
		
		_cal.clear(Calendar.HOUR_OF_DAY);
		_cal.clear(Calendar.AM_PM);
		_cal.clear(Calendar.MINUTE);
		_cal.clear(Calendar.SECOND);
		_cal.clear(Calendar.MILLISECOND);
		
		// Month 0 is January, 2 is March, 3 is April
		_cal.set( Calendar.MONTH, 2 );  		
		_cal.set( Calendar.DAY_OF_MONTH, _cal.getActualMaximum( Calendar.DAY_OF_MONTH ));		
		return _cal.getTime();
	}
	*/
	
	public static Date addDay(Date date, int number) {
		return calculate(date, number, DAY);
	}
	
	public static Date addMonth(Date date, int number) {
		return calculate(date, number, MONTH);
	}
	
	public static Date addYear(Date date, int number) {
		return calculate(date, number, YEAR);
	}
	
	private static Date calculate(Date date, int number, String type) {
		
		int _type = Calendar.DATE;
		if (MONTH.equals(type))
			_type = Calendar.MONTH;
		else if (YEAR.equals(type))
			_type = Calendar.YEAR;
		
		// Date _newDate = new Date( date.getTime() );
		
		Calendar _cal = Calendar.getInstance();
//		_cal.setTime(_newDate);
		_cal.setTime( date );
		
		_cal.add( _type, number);
		return _cal.getTime();
	}
	
	private static Date convertToUTC2(Date date) {
	    // **** YOUR CODE **** BEGIN ****
	    Date localTime = date;
	    String format = "yyyy/MM/dd HH:mm:ss";
	    
	    SimpleDateFormat sdf = new SimpleDateFormat(format);		
	    // Convert Local Time to UTC (Works Fine)
	    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	    Date gmtTime = new Date(sdf.format(localTime));
	    //System.out.println("Local:" + localTime.toString() + "," + localTime.getTime() + " --> UTC time:"
	    //        + gmtTime.toString() + "," + gmtTime.getTime());		
	    return gmtTime;
	}
	
	public static Date convertToUTC(Date date) {
		Calendar _cal = Calendar.getInstance();
		_cal.setTime( date );

		_cal.add( Calendar.HOUR, 8);
		
		return _cal.getTime();

	}		
	
	/**
	 * This function to test if the input date is the 1st day of the month
	 * @param date
	 * @return
	 */
	public static boolean isFirstDayofMonth(Date date) {
		Calendar _cal = Calendar.getInstance();
		_cal.setTime( date );
		_cal.set(Calendar.HOUR_OF_DAY,0);
		_cal.set(Calendar.AM_PM, Calendar.AM);
		_cal.set(Calendar.MINUTE,0);
		_cal.set(Calendar.SECOND,0);
		_cal.set(Calendar.MILLISECOND,0);
		
		Calendar firstDayofMonth = Calendar.getInstance();
		firstDayofMonth.setTime( _cal.getTime() );
		firstDayofMonth.set( Calendar.DAY_OF_MONTH, 1);
		
		if ( firstDayofMonth.equals( _cal )) {
			return true;
		} else {
			return false;
		}
		
	}
}
