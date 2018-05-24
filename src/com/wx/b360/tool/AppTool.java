package com.wx.b360.tool;
/**
 * 综合工具类
 * @author 莫小雨
 *
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.data.domain.Page;

public class AppTool {
	//将Page中的内容和总条数取出
	public static Map<String, Object> pageToMap(Page page) {
		Map<String, Object> result = new HashMap<>();
		result.put("content", page.getContent());
		result.put("count", page.getTotalElements());
		return result;
	}
	/**
	 * 将日期格式化为yyyy-MM-dd
	 * @param dateStr
	 * @return
	 */
	public static Date changeDate(String dateStr) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return format.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	/**
	 * 获取今天的第一秒和最后一秒的时间戳
	 * @return
	 */
	public static ArrayList<Date> getTodayBound() {
		long current = System.currentTimeMillis();
		long zero = current/(1000*3600*24)*(1000*3600*24)-TimeZone.getDefault().getRawOffset();
		long twelve=zero+24*60*60*1000-1;//今天23点59分59秒的毫秒数
		Date startTime = new Date(zero);
		Date endTime = new Date(twelve);

		ArrayList<Date> arrayList = new ArrayList<>();
		arrayList.add(startTime);
		arrayList.add(endTime);

		return arrayList;
	}
	
	/**
	 * 获取今天的第一秒的时间戳
	 * @return
	 */
	public static Date getTodayStart() {
		long current = System.currentTimeMillis();
		long zero = current/(1000*3600*24)*(1000*3600*24)-TimeZone.getDefault().getRawOffset();
		Date startTime = new Date(zero);
		return startTime;
	}
	/**
	 * 获取今天最后一秒的时间戳
	 * @return
	 */
	public static Date getTodayStop() {
		long current = System.currentTimeMillis();
		long zero = current/(1000*3600*24)*(1000*3600*24)-TimeZone.getDefault().getRawOffset();
		long twelve=zero+24*60*60*1000-1;//今天23点59分59秒的毫秒数
		Date endTime = new Date(twelve);

		return endTime;
	}
	
	

	// 获得本周一0点时间
	public static Date getTimesWeekmorning() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal.getTime();
	}

	// 获得本周日24点时间
	public static Date getTimesWeeknight() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getTimesWeekmorning());
		cal.add(Calendar.DAY_OF_WEEK, 7);
		return cal.getTime();
	}

	// 获得本月第一天0点时间
	public static Date getTimesMonthmorning() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	// 获得本月最后一天24点时间
	public static Date getTimesMonthnight() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 24);
		return cal.getTime();
	}
	
}
