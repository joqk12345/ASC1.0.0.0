/*******************************************************************************
Copyright (C) Autelan Technology


This software file is owned and distributed by Autelan Technology 
********************************************************************************/
package com.asc.app.util;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;


/**
 * 日期操作工具类
 */
public class DateUtil {
	public DateUtil() {

	}
	/**
	 * 转换告警时间为Date格式,time为2010/12/8-10:8:1格式
	 */
	public static Date getAlarmTime(String time) {
		StringBuffer newTime = new StringBuffer();
		String[] times = time.split("[/:-]");
		
		newTime.append(times[0]+"-");
		newTime.append(times[1].length()==1?"0"+times[1]:times[1]);
		newTime.append("-");
		newTime.append(times[2].length()==1?"0"+times[2]:times[2]);
		newTime.append(" ");
		newTime.append(times[3].length()==1?"0"+times[3]:times[3]);
		newTime.append(":");
		newTime.append(times[4].length()==1?"0"+times[4]:times[4]);
		newTime.append(":");
		newTime.append(times[5].length()==1?"0"+times[5]:times[5]);
		time = newTime.toString();
		return strToDate(time);
	}
	/**
	 * 获得一周前的时间
	 * @return
	 */
	public static String getOneWeekBefore() {
		GregorianCalendar today = new GregorianCalendar();
		today.add(Calendar.DAY_OF_MONTH, -7);
		SimpleDateFormat format = new SimpleDateFormat(
		"yyyy.MM.dd HH:mm:ss");
		String s = format.format(today.getTime());
		
		return s;
		
		
	}
	/**
	 * 格式化为yyyy-MM-dd HH:mm:ss
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		return format(date,"yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * 自定义格式化字符串
	 * @param date 待格式化的时间
	 * @param pattern 格式字符串
	 * @return
	 */
	public static String format(Date date,String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}

	/**
	 * 得到当前时间值，格式：yyyy年MM月dd日HH时mm分ss秒
	 * 
	 * @return String值
	 */
	public static String getNowTime() {
		try {
			Date date = new Date();
			SimpleDateFormat simpledateformat = new SimpleDateFormat(
					"yyyy.MM.dd HH:mm:ss");
			String s = simpledateformat.format(date);
			return s;
		} catch (Exception e) {
			return "";
		}
	}
	public static void main(String[] args){
		System.out.println(getOneWeekBefore());
	}
	/**
	 * 得到当前日期值，格式：yyyy年MM月dd日
	 * 
	 * @return String值
	 */
	public static String getNowDate() {
		try {
			Date date = new Date();
			SimpleDateFormat simpledateformat = new SimpleDateFormat(
					"yyyy年MM月dd日");
			String s = simpledateformat.format(date);
			return s;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 返回oracle DATE 格式数据供数据库进行insert update 操作时使用
	 * 
	 * @param lTimeValue
	 *            时间的整数值
	 * @return
	 */
	public static String getDateFormatofOracle(long lTimeValue) {

		Date dt = new Date(lTimeValue);
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String tmpTime = sdFormat.format(dt).toString();
		String time = "to_date('" + tmpTime + "','YYYY-MM-DD HH24:MI:SS')";
		return time;
	}

	/**
	 * 返回DATE 格式数据供数据库进行insert update 操作时使用,对Oracle自动添加TO_DATE，对Sqlserver自动添加单引号
	 * 
	 * @param sTimeValue 时间的YYYY-MM-DD HH24:MI:SS格式表示
	 * @return
	 
	public static String getDateFormat(long lTimeValue) {
		if(DbConnectionManager.isOracle()){
			return getDateFormatofOracle(lTimeValue);
		}
		else{
			Date dt = new Date(lTimeValue);
			SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String tmpTime = sdFormat.format(dt).toString();
			
			return "'"+tmpTime+"'";
		}
	}*/
	/**
	 * 返回DATE 格式数据供数据库进行insert update 操作时使用,对Oracle自动添加TO_DATE，对Sqlserver自动添加单引号
	 * 
	 * @param sTimeValue 时间的YYYY-MM-DD HH24:MI:SS格式表示
	 * @return
	 
	public static String getDateFormat(String sTimeValue) {
		if(DbConnectionManager.isOracle()){
			String time = "to_date('" + sTimeValue + "','YYYY-MM-DD HH24:MI:SS')";
			return time;
		}
		else{
			return "'"+sTimeValue+"'";
		}
	}
	*/
	
	/**
	 * 返回数据库特有的字符串到日期的带参数的sql语句.
	 * 主要用于构造update语句时使用
	 * @return
	 * 		if oracle return "to_date(?,'YYYY-MM-DD HH24:MI:SS')"
	 * 		if sql server return "?"
	 * @see getDateFormatForQuery(String col)
	 
	public static String getDateFormatForUpdate() {
		if(DbConnectionManager.isOracle()){
			String time = "to_date(?,'YYYY-MM-DD HH24:MI:SS')";
			return time;
		}
		else{
			return "?";
		}
	}
	*/
	/**
	 * 返回数据库特有的日期到字符串的sql语句
	 * 主要用于构造select语句时使用
	 * @param sTimeValue
	 * @return
	 * 		if oracle return "to_char(starttime,'YYYY-MM-DD HH24:MI:SS')"
	 * 		if sql server return "convert(char(19),starttime, 20)"
	 * @see getDateFormatForUpdate()
	 
	public static String getDateFormatForQuery(String sTimeValue) {
		if(DbConnectionManager.isOracle()){
			String time = "to_char(" + sTimeValue + ",'YYYY-MM-DD HH24:MI:SS')";
			return time;
		}
		else if(DbConnectionManager.isSqlServer()){
			return "convert(char(19)," + sTimeValue + ", 20)";
		}
		return "";
	}	
	*/
	
	
	/**
	 * 返回oracle DATE 格式数据供数据库进行insert update 操作时使用
	 * 
	 * @param sTimeValue
	 *            时间的YYYY-MM-DD HH24:MI:SS格式表示
	 * @return
	 */
	public static String getDateFormatofOracle(String sTimeValue) {
		String time = "to_date('" + sTimeValue + "','YYYY-MM-DD HH24:MI:SS')";
		return time;
	}
	

	
	/**
	 * 返回指定Date值的时间值，格式：yyyy年MM月dd日HH时mm分ss秒
	 * 
	 * @param date
	 *            需要转换的日期
	 * @return String值
	 */
	public static String dateToString(Date date){
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat(
					"yyyy年MM月dd日HH时mm分ss秒");
			String s = simpledateformat.format(date);
			return s;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 
	 * 返回指定Date值的时间值
	 * 
	 * @param date
	 *            需要转换的日期
	 * @param pattern
	 *            时间格式
	 * @return String值
	 */
	public static String dateToStringCus(Date date, String pattern) {
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat(pattern);
			String s = simpledateformat.format(date);
			return s;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 返回指定Date值的时间值，格式：yyyy-MM-dd HH:mm
	 * 
	 * @param date
	 *            需要转换的日期
	 * @return String值
	 */
	public static String dateToCode(Date date) {
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			String s = simpledateformat.format(date);
			return s;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 返回指定Date值的时间值，格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 *            需要转换的日期
	 * @return String值
	 */
	public static String dateToAllCode(Date date) {
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String s = simpledateformat.format(date);
			return s;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 返回指定Date值的时间值，格式：yyyy-MM-dd HH
	 * 
	 * @param date
	 *            需要转换的日期
	 * @return String值
	 */
	public static String dateToPartAllCode(Date date) {
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat(
					"yyyy-MM-dd HH");
			String s = simpledateformat.format(date);
			return s;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 返回指定Date值的时间值，格式：yyyy-MM
	 * 
	 * @param date
	 *            需要转换的日期
	 * @return String值
	 */
	public static String dateToShortCodeM(Date date) {
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat(
					"yyyy-MM");
			String s = simpledateformat.format(date);
			return s;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 返回指定Date值的时间值，格式：yyyyMMddHHmmss
	 * 
	 * @param date
	 *            需要转换的日期
	 * @return String值
	 */
	public static String dateToShortAllCode(Date date) {
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat(
					"yyyyMMddHHmmss");
			String s = simpledateformat.format(date);
			return s;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 返回指定Date值的时间值，格式：HH:mm:ss
	 * 
	 * @param date
	 *            需要转换的日期
	 * @return String值
	 */
	public static String dateToTimeCode(Date date) {
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat("HH:mm:ss");
			String s = simpledateformat.format(date);
			return s;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 返回指定Date值的时间值，格式：yyyy年MM月dd日
	 * 
	 * @param date
	 *            需要转换的日期
	 * @return String值
	 */
	public static String dateToShortString(Date date) {
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat(
					"yyyy年MM月dd日");
			String s = simpledateformat.format(date);
			return s;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 返回指定Date值的时间值，格式：yyyy-MM-dd
	 * 
	 * @param date
	 *            需要转换的日期
	 * @return String值
	 */
	public static String dateToShortCode(Date date) {
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat(
					"yyyy-MM-dd");
			String s = simpledateformat.format(date);
			return s;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 按“-”分割指定Date值
	 * 
	 * @param date
	 *            需要转换的日期
	 * @return String[]值
	 */
	public static String[] SplitDate(Date date) {
		String s = dateToShortCode(date);
		String[] temp = new String[3];
		StringTokenizer st = new StringTokenizer(s, "-");
		int i = 0;
		while (st.hasMoreTokens()) {
			temp[i] = st.nextToken();
			i++;
		}
		return temp;
	}

	/**
	 * 转换字符串为Date值，字符串格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @param s
	 *            需要转换的字符串，字符串格式：yyyy-MM-dd HH:mm:ss
	 * @return Date值
	 */
	public static Date strToDate(String s) {
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			ParsePosition parseposition = new ParsePosition(0);
			Date date = simpledateformat.parse(s);
			return date;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**转换字符串为两个Date值，如果传入2010-10-12 23:09:00，则返回时间2010-10-12 22:00:00和2010-10-12 23:00:00
	 * 
	 * @param time 需要转换的字符串，字符串格式：yyyy-MM-dd HH:mm:ss
	 * @author liushiquan
	 */
	public static String[] strToDate2(String time){
		try{
			SimpleDateFormat simpledateformat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
			Date date=simpledateformat.parse(time);
			GregorianCalendar c=new GregorianCalendar();
			c.setTime(date);
			c.add(Calendar.HOUR_OF_DAY, -1);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			
			String start=simpledateformat.format(new Date(c.getTimeInMillis()));
			
			c.add(Calendar.HOUR_OF_DAY, 1);
			String end=simpledateformat.format(new Date(c.getTimeInMillis()));
			
			return new String[]{start,end};
		
		}
		catch(Exception e){
			e.printStackTrace();
			
		}
		return null;
	}
	/**
	 * 转换字符串为Date值，字符串格式：HH:mm:ss
	 * 
	 * @param s
	 *            需要转换的字符串，字符串格式：HH:mm:ss
	 * @return Date值
	 */
	public static Date timeCodeToDate(String s) {
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat("HH:mm:ss");
			ParsePosition parseposition = new ParsePosition(0);
			Date date = simpledateformat.parse(s);
			return date;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 转换字符串为Date值，字符串格式：yyyy-MM-dd
	 * 
	 * @param s
	 *            需要转换的字符串，字符串格式：yyyy-MM-dd
	 * @return Date值
	 */
	public static Date strToShortday(String s) {
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat(
					"yyyy-MM-dd");
			ParsePosition parseposition = new ParsePosition(0);
			Date date = simpledateformat.parse(s, parseposition);
			return date;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 转换字符串为Date值，字符串格式：yyyy-MM-dd HH
	 * 
	 * @param s
	 *            需要转换的字符串，字符串格式：yyyy-MM-dd HH
	 * @return Date值
	 */
	public static Date strToShorthour(String s) {
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat(
					"yyyy-MM-dd HH");
			ParsePosition parseposition = new ParsePosition(0);
			Date date = simpledateformat.parse(s, parseposition);
			return date;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 返回当前Date对象
	 * 
	 * @return Date值
	 */
	public static Date getNow() {
		Date date = new Date();
		return date;
	}

	/**
	 * 转换日期字符串为long值，字符串格式：yyyy-MM-dd
	 * 
	 * @param s
	 *            需要转换的字符串，字符串格式：yyyy-MM-dd
	 * @return long值
	 */
	public static long getS(String s) {
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat(
					"yyyy-MM-dd");
			ParsePosition parseposition = new ParsePosition(0);
			Date date = simpledateformat.parse(s, parseposition);
			return date.getTime();
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * 转换日期字符串为long值，字符串格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @param s
	 *            需要转换的字符串，字符串格式：yyyy-MM-dd HH:mm:ss
	 * @return long值
	 */
	public static long getStrToLong(String s) {
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			ParsePosition parseposition = new ParsePosition(0);
			Date date = simpledateformat.parse(s, parseposition);
			return date.getTime();
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * 得到long类型日期值
	 * 
	 * @return long值
	 */
	public static long getLongTime() {
		return (new Date()).getTime();
	}

	/**
	 * 得到某个时间距离现在的分钟数
	 * 
	 * @param l
	 *            日期long类型值
	 * @return long值
	 */
	public static long getOffMinutes(long l) {
		return getOffMinutes(l, System.currentTimeMillis());
	}

	/**
	 * 得到两个时间之间的时间差，单位为：分钟
	 * 
	 * @param l
	 *            第一个日期long类型值
	 * @param l1
	 *            第二个日期long类型值
	 * @return long值
	 */
	public static long getOffMinutes(long l, long l1) {
		return (long) ((l1 - l) / 60000L);
	}

	/**
	 * 根据指定时间格式字符串（格式:yyyy-MM-dd），取得该时间对应的周一至周日Date对象 Date first =
	 * DateUtil.getMonday(today,Calendar.MONDAY); Date last =
	 * DateUtil.getMonday(today,Calendar.SUNDAY);
	 * 
	 * @param date
	 *            String类型，格式:yyyy-MM-dd
	 * @param weekDay
	 *            一周的第几天，第一天为Calendar.MONDAY，第七天为Calendar.SUNDAY
	 * @return Date值
	 */
	public static Date getMonday(String date, int weekDay) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date d = null;
		try {
			d = format.parse(date);
		} catch (Exception e) {
		//	log.error("异常：", e);
		}
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.setTime(d);
		// ����DAY_OF_WEEK��˵��
		// Field number for get and set indicating
		// the day of the week. This field takes values
		// SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY,
		// and SATURDAY
		cal.set(Calendar.DAY_OF_WEEK, weekDay);
		//cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}

	/**
	 * 根据指定时间格式字符串（格式:yyyy-MM），取得该时间对应月份第一天Date对象
	 * 
	 * @param date
	 *            String类型，格式:yyyy-MM
	 * @return Date值
	 */
	public static Date getMonthFirstDay(String date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		Date d = null;
		try {
			d = format.parse(date);
		} catch (Exception e) {
		//	log.error("异常：", e);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DAY_OF_MONTH, 0);
		return cal.getTime();
	}

	/**
	 * 根据指定时间格式字符串（格式:yyyy-MM），取得该时间对应月份最后一天Date对象
	 * 
	 * @param date
	 *            String类型，格式:yyyy-MM
	 * @return Date值
	 */
	public static Date getMonthLastDay(String date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		Date d = null;
		try {
			d = format.parse(date);
		} catch (Exception e) {
		//	log.error("异常：", e);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	/**
	 * 根据指定时间格式字符串（格式:yyyy），取得该时间对应年份第一天Date对象
	 * 
	 * @param date
	 *            String类型，格式:yyyy
	 * @return Date值
	 */
	public static Date getYearFirstDay(String date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		Date d = null;
		try {
			d = format.parse(date);
		} catch (Exception e) {
		//	log.error("异常：", e);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DAY_OF_YEAR, 0);
		return cal.getTime();
	}

	/**
	 * 根据指定时间格式字符串（格式:yyyy），取得该时间对应年份最后一天Date对象
	 * 
	 * @param date
	 *            String类型，格式:yyyy
	 * @return Date值
	 */
	public static Date getYearLastDay(String date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		Date d = null;
		try {
			d = format.parse(date);
		} catch (Exception e) {
		//	log.error("异常：", e);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.YEAR, 1);
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	/**
	 * 转换long值为字符串类型日期格式
	 * 
	 * @param l
	 *            long值
	 * @return String值
	 */
	public static String LongToDateString(long l) {
		DateFormat mediumDateFormat = null;
		Date sDate = null;
		try {
			mediumDateFormat = DateFormat.getDateTimeInstance();
			String date = String.valueOf(l);
			sDate = new Date(Long.parseLong(date));
		} catch (Exception ex) {
		//	log.error("异常：", ex);
			return null;
		}
		return mediumDateFormat.format(sDate);
	}
	
	public static int getTimeSpace(long newTime, long oldTime) {
		
		int result = -1;
		
		if(newTime>=oldTime){
			long space = newTime - oldTime;
			long longResult = space / 1000 / 60 / 60 / 24;
			result = (int)longResult;
		}

		return result;
	}
	
	public static String getTimeSpace(Date date1, Date date2){
			StringBuffer result = new StringBuffer();
		   long l = date1.getTime()-date2.getTime();
		   long day = l/(24*60*60*1000);
		   long hour =(l/(60*60*1000)-day*24);
		   long min =((l/(60*1000))-day*24*60-hour*60);
		   double s = (l/1000-day*24*60*60-hour*60*60-min*60);
		   if (day>0) {
			   result.append(day+"天");
		}
		   if (hour>0) {
			   result.append(hour+"小时");
		}
		   if (min>0) {
			result.append(min+"分");
		}
		   result.append(l+"毫秒");
		return result.toString();
	}
	
	public static String getTSpaceInM(Date date1, Date date2){
		
		  long l = date1.getTime()-date2.getTime();
		  long min = l/(60*1000);
		  return String.valueOf(min);
		  
		  
	}
	
	//比较日期先后顺序
	//返回值 0 表示时间格式有误
	//返回值 1 表示前者先于后者
	//返回值 2 表示后者先于前者
	public static int compareTime(String date1, String date2){
		
		int result = 0;
		
		try {
			long lDate1 = strToDate(date1).getTime();
			long lDate2 = strToDate(date2).getTime();
			
			if(lDate1<lDate2){
				result = 1;
			}else{
				result = 2;
			}
		}catch(Exception e){
			result = 0;
		}

		return result;
		
	}
	
	//比较指定日期与当前日期的先后顺序
	//返回值 0 表示时间格式有误
	//返回值 1 表示指定日期先于当前日期
	//返回值 2 表示当前日期先于指定日期
	public static int compareTimeToCurrent(String date){
		
		int result = 0;
		
		try {
			long lDate1 = strToDate(date).getTime();
			long lDate2 = getLongTime();
			if(lDate1<lDate2){
				result = 1;
			}else{
				result = 2;
			}
		}catch(Exception e){
			result = 0;
		}

		return result;
		
	}
	/**
	 * 取得当前时间的Oracle格式
	 * @return
	
	public static String getCurrentDateFormatofOracle(){
		return getDateFormatofOracle(getLongTime());
	}

	public static String getCurrentDate() {
		if(DbConnectionManager.isOracle()){
			return getCurrentDateFormatofOracle();
		}
		else{
			return "getdate()";
		}
	}
	public static String getCommitString() {
		if(DbConnectionManager.isOracle()){
			return "COMMIT;";
		}
		else{
			return "";
		}
	}
	 */
	
	/**
	 * 得到前几个小时的时间
	 */
	public static String getLastHourDate(String dateTime,int n){
		
		 //String dateTime = "2008-03-31 00:12:12";
		  
		  int intYear = Integer.parseInt(dateTime.substring(0, 4));
		  int intMonth = Integer.parseInt(dateTime.substring(5, 7));
		  int intDate = Integer.parseInt(dateTime.substring(8, 10));
		  int intHour= Integer.parseInt(dateTime.substring(11, 13));
		  int intMin= Integer.parseInt(dateTime.substring(14, 16));
		  int intSec= Integer.parseInt(dateTime.substring(17, 19));
		  
		  Calendar cal = Calendar.getInstance();
		  //cal.set(intYear, intMonth - 1, intDate);
		  cal.set(intYear, intMonth-1, intDate, intHour,intMin,intSec);
		  cal.add(Calendar.HOUR, -n);
		  
		  //Date tempDate = cal.getTime();
		  //Long time = tempDate.getTime()/1000) - 60*60*24;
		  //tempDate .setTime(startTime*1000);
		  
		  Date date = cal.getTime();
		  DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  String LastOneHourDate=df.format(date);
		  //System.out.print(df.format(date));
		return LastOneHourDate;
	}
}