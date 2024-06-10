/*
 * Copyright 2012 Tauasa Timoteo
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation 
 * files (the “Software”), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-
 * INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN 
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF 
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 */
package org.tauasa.commons.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Utility class for working with the shitty Java {@link Date} and {@link Calendar} classes
 *
 * @author Tauasa Timoteo
 * 
 */
public class DateUtils extends Utils {

	public static final String MMDDYYYY = "MM/dd/yyyy";
	public static final String MMDDYYYYHHMM = MMDDYYYY+" h:mm a";
	public static final String MMDDYYYYHHMMSS = MMDDYYYY+" h:mm:ss a";
	public static final String LENIENT_DATE_PARSING_KEY = "tui.lenient.date.parsing";
	private static final Map<String, DateFormat> FORMATTERS = new HashMap<>();
	//private static final boolean LENIENT_DATE_PARSING = Boolean.TRUE;
	private static final DateTimeFormatter XML_DATE_TIME_FORMATTER = ISODateTimeFormat.dateTimeNoMillis().withOffsetParsed();

	private static boolean isLenientDateParsing(){
		return Boolean.parseBoolean(System.getProperty(LENIENT_DATE_PARSING_KEY, "true"));
	}

	/**
	 * Creates a {@link DateFormat} for the specified pattern. By default the
	 * {@link DateFormat} has lenient parsing enabled but can be overridden
	 * by setting the system property <code>tui.lenient.date.parsing</code> to false.
	 * */
	public static final DateFormat createDateFormat(String pattern){
		return createDateFormat(pattern, TimeZone.getDefault(), isLenientDateParsing());
	}

	/**
	 * Parse the specified ISO8601-formatted date string into a {@link Date}
	 * */
	public static final Date parseOrdinalDateString(String s){
		if(isEmpty(s)){
			return null;
		}
		DateTimeFormatter fmt = ISODateTimeFormat.basicOrdinalDate();
		DateTime dt = fmt.parseDateTime(s);
		return new Date(dt.getMillis());
	}

	/**
	 * Returns an ISO8601-formatted string for the specified {@link Date} or null
	 * if the date is null
	 * */
	public static final String toOrdinalDateString(Date ts){
		if(ts==null){
			return null;
		}
		return ISODateTimeFormat.basicOrdinalDate().print(ts.getTime());
	}

	/**
	 * Create a DateFormat for the specified pattern and leniency
	 * */
	public static final DateFormat createDateFormat(String pattern, TimeZone tz, boolean lenient){
		DateFormat df = FORMATTERS.get(pattern);
		if(df==null){
			df = new SimpleDateFormat(pattern);
			df.setLenient(lenient);
			if(tz!=null){
				df.setTimeZone(tz);
			}
			FORMATTERS.put(pattern, df);
		}
		return df;
	}

	/**
	 * Parses the specified {@link String}, which should be in a lexical XML date format,
	 * into a {@link Date} (which will be adjusted to GMT)
	 * */
	public static final Date parseXSDDateTime(String lexicalXSDDateTime){
		return parseXSDDateTime(lexicalXSDDateTime, null);
	}

	/**
	 * Parses the specified {@link String}, which should be in a lexical XML date format,
	 * into a {@link Date} and adjusts the time to the specified time zone
	 * */
	public static final Date parseXSDDateTime(String lexicalXSDDateTime, TimeZone tz){
		if(isEmpty(lexicalXSDDateTime)){
			return null;
		}
		DateTime dt = XML_DATE_TIME_FORMATTER.parseDateTime(lexicalXSDDateTime);
		if(tz!=null){
			//adjust to the specified time zone if one is specified
			dt = dt.withZoneRetainFields(DateTimeZone.forTimeZone(tz));
		}
		return dt.toDate();
	}

	/*
	public static final Date parseXSDDateTime(String lexicalXSDDateTime){
		if(isEmpty(lexicalXSDDateTime)){
			return null;
		}
		return DatatypeConverter.parseDateTime(lexicalXSDDateTime).getTime();
	}//*/

	/**
	 * Returns true if the first {@link Date} is after the second {@link Date} according to the {@link Date#after(Date)} method.
	 * If {@code compareTime} is false then times for both dates are zero'd out before the comparison
	 * */
	public static boolean isAfter(Date d1, Date d2, boolean compareTime){
		if(d1==null || d2==null){
			throw new NullPointerException("One of the dates being compared is null");
		}
		if(!compareTime){
			return setTime(d1, 0, 0, 0).after(setTime(d2, 0, 0, 0));
		}
		return d1.after(d2);
	}

	public static boolean isOnOrAfter(Date d1, Date d2, boolean compareTime){
		if(d1==null || d2==null){
			throw new NullPointerException("One of the dates being compared is null");
		}
		if(!compareTime){
			Date _d1 = setTime(d1, 0, 0, 0);
			Date _d2 = setTime(d2, 0, 0, 0);
			return _d1.equals(_d2) || _d1.after(_d2);
		}
		return d1.equals(d2) || d1.after(d2);
	}

	public static boolean isBefore(Date d1, Date d2, boolean compareTime){
		if(d1==null || d2==null){
			throw new NullPointerException("One of the dates being compared is null");
		}
		if(!compareTime){
			return setTime(d1, 0, 0, 0).before(setTime(d2, 0, 0, 0));
		}
		return d1.before(d2);
	}

	public static boolean isOnOrBefore(Date d1, Date d2, boolean compareTime){
		if(d1==null || d2==null){
			throw new NullPointerException("One of the dates being compared is null");
		}
		if(!compareTime){
			Date _d1 = setTime(d1, 0, 0, 0);
			Date _d2 = setTime(d2, 0, 0, 0);
			return _d1.equals(_d2) || _d1.before(_d2);
		}
		return d1.equals(d2) || d1.before(d2);
	}

	/**
	 * Adjusts the time for the specified to midnight. If the date is null a new date is created for the
	 * current day and zero'd to midnight
	 * */
	public static Date zero(Date date){
		if(date==null){
			return zero();
		}
		return setTime(date, 0, 0, 0);
	}

	/**
	 * Creates a new date representing midnight for the current day
	 * */
	public static Date zero(){
		return setTime(new Date(), 0, 0, 0);
	}

	/**
	 * Shorthand for {@link DateUtils#formatDate(d, MMDDYYYYHHMMSS)}
	 * */
	public static String toMMDDYYYYHHMMSS(Date d){
		return formatDate(d, MMDDYYYYHHMMSS);
	}

	/**
	 * Shorthand for {@link DateUtils#formatDate(d, MMDDYYYY)}
	 * */
	public static String toMMDDYYYY(Date d){
		return formatDate(d, MMDDYYYY);
	}

	/**
	 * Shorthand for {@link DateUtils#formatDate(d, MMDDYYYYHHMM)}
	 * */
	public static String toMMDDYYYYHHMM(Date d){
		return formatDate(d, MMDDYYYYHHMM);
	}

	/**
	 * Shorthand for {@link DateUtils#parseDate(String, MMDDYYYY)}
	 * */
	public static Date parseMMDDYYYY(String ts)throws ParseException{
		return parseDate(ts, MMDDYYYY);
	}

	/**
	 * Formats the specified date
	 * */
	public static final String formatDate(Date d, String pattern){
		if(d==null){
			return null;
		}
		return createDateFormat(pattern).format(d);
	}

	public static Date parse(String ts){
		try{
			return parseDate(ts, MMDDYYYY);
		}catch(ParseException e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * Parses the a date from the specified string using the specified pattern.
	 * Returns null of the specified date string (ts) is null or 0-length
	 * */
	public static final Date parseDate(String ts, String pattern)throws ParseException{
		if (isEmpty(ts)) {
			return null;
		}
		return createDateFormat(pattern).parse(ts);
	}

	/**
	 * Adjusts the specified date the specified number of days (use a negative value to
	 * subtract days)
	 * */
	public static Date adjustDate(Date ts, int numDays){
		Calendar c = Calendar.getInstance();
		c.setTime(ts);
		c.add(Calendar.DATE, numDays);
		return c.getTime();
	}

	public static Date adjustToLastDayOfMonth(Date ts){
		Calendar c = Calendar.getInstance();
		c.setTime(ts);
		c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
		return c.getTime();
	}

	/**
	 * Sets the time for the specified date (using a 24-hour clock). The milliseconds
	 * field is set to 0
	 * */
	public static Date setTime(Date ts, int hour, int minutes, int seconds){
		Calendar c = Calendar.getInstance();
		c.setTime(ts);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minutes);
		c.set(Calendar.SECOND, seconds);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	public static Date adjustMonth(Date ts, int numMonths){
		Calendar c = Calendar.getInstance();
		c.setTime(ts);
		c.add(Calendar.MONTH, numMonths);
		return c.getTime();
	}

	public static Date setYear(Date ts, int year){
		Calendar c = Calendar.getInstance();
		c.setTime(ts);
		c.set(Calendar.YEAR, year);
		return c.getTime();
	}

	/**
	 * Returns the difference in years between the specified dates
	 * */
	public static long getDiffInYears(Date d1, Date d2){
		return getDiffInDays(d1, d2)/365L;
	}

	/**
	 * Returns the difference in seconds between the specified dates
	 */
	public static long getDiffInSeconds(Date d1, Date d2){
		if(d1==null || d2==null){
			throw new RuntimeException("One or more null dates specified");
		}
		return Math.abs(d1.getTime()-d2.getTime())/1000L;
	}

	/**
	 * Returns the difference in minutes between the specified dates
	 * */
	public static long getDiffInMinutes(Date d1, Date d2){
		return getDiffInSeconds(d1, d2)/60L;
	}

	/**
	 * Returns the difference in hours between the specified dates
	 */
	public static long getDiffInHours(Date d1, Date d2){
		return getDiffInMinutes(d1, d2)/60L;
	}

	/**
	 * Returns the difference in days between the specified dates
	 */
	public static long getDiffInDays(Date d1, Date d2){
		return getDiffInHours(d1, d2)/24L;
	}

	/**
	 * Returns true if the specified date falls on a Saturday or Sunday
	 * */
	public static final boolean isWeekendDay(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int day = c.get(Calendar.DAY_OF_WEEK);
		return day==Calendar.SATURDAY || day==Calendar.SUNDAY;
	}

	public static int getCurrentYear(){
		return getYear(new Date());
	}

	public static int getCurrent2DigitYear(){
		return getYear(new Date());
	}

	public static int getCurrentMonth(){
		return getMonth(new Date());
	}

	public static int getCurrentDayOfMonth(){
		return getDayOfMonth(new Date());
	}

	/**
	 * Returns the year of the specified date as a 2-digit string
	 * */
	public static String getYear2Digit(Date ts){
		return formatDate(ts, "yy");
	}

	/**
	 * Returns the year from the specified date
	 */
	public static int getYear(Date ts){
		if (ts==null) {
			return getYear(new Date());
		}
		Calendar c = Calendar.getInstance();
		c.setTime(ts);
		return c.get(Calendar.YEAR);
	}

	public static int getMonth(Date ts){
		if (ts==null) {
			return getMonth(new Date());
		}
		Calendar c = Calendar.getInstance();
		c.setTime(ts);
		return c.get(Calendar.MONTH);
	}

	public static int getDayOfMonth(Date ts){
		if (ts==null) {
			return getDayOfMonth(new Date());
		}
		Calendar c = Calendar.getInstance();
		c.setTime(ts);
		return c.get(Calendar.DAY_OF_MONTH);
	}

}
