package com.wx.b360.tool;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.sun.xml.internal.ws.api.server.SDDocumentFilter;


/**
 * 检测输入字串是否正确的工具类
 * @author Slience
 *
 */
public class CheckTool {

	private static SimpleDateFormat format;

	
	public static boolean isDate(String date) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			format.parse(date);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	public static boolean isHKPhoneLegal(String str) {    
		if(!isString(str)) {
			return true;
		}
		String regExp = "^(5|6|8|9)\\d{7}$";    
		Pattern p = Pattern.compile(regExp);    
		Matcher m = p.matcher(str);    
		return !m.matches();    
	} 

	/**
	 * 验证是否是XX:XX时分
	 * @param str
	 * @return
	 */
	public static boolean Time(String str) {
		String[] time = str.split(":");
		if(time.length != 2) {
			return true;
		} else {
			if(Integer(time[0]) || Integer(time[1])) {
				return true;
			} else {
				if(Integer.parseInt(time[0]) > 60 || Integer.parseInt(time[1]) > 60) {
					return true;
				} else {
					return false;
				}
			}
		}
	}

	/**
	 * 验证身份证
	 * @param str
	 * @return
	 */
	public static boolean isID(String str) {
		if(str != null) {
			Pattern pattern = Pattern.compile("^(^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$)|(^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])((\\d{4})|\\d{3}[Xx])$)$");
			Matcher matcher = pattern.matcher(str);
			return matcher.matches();
		} else {
			return false;
		}
	}
	/**
	 * 验证邮箱
	 *
	 * @param email
	 * @return
	 */

	public static boolean isEmail(String email) {
	    boolean flag = false;
	    try {
	        String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	        Pattern regex = Pattern.compile(check);
	        Matcher matcher = regex.matcher(email);
	        flag = matcher.matches();
	    } catch (Exception e) {
	        flag = false;
	    }
	    return flag;
	}

	/**
	 * 验证手机号
	 * @param str
	 * @return
	 */
	public static boolean isPhone(String str) {
		if(isString(str)) {
			//注释的是香港手机号
//			if(str.length() == 8) {
//				String regExp = "^(5|6|8|9)\\d{7}$";    
//				Pattern p = Pattern.compile(regExp);    
//				Matcher m = p.matcher(str);  
//				return !m.matches();
//			}
			
			Pattern pattern = Pattern.compile("^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$");
			Matcher matcher = pattern.matcher(str);
			return matcher.matches();
		} else {
			return false;
		}
	}

	/**验证验证码
	 * 
	 * @param code
	 * @return
	 */
	public static boolean Code(String str) {
		if(str != null) { 
			Pattern pattern = Pattern.compile("^\\d{4}$");
			Matcher matcher = pattern.matcher(str);
			return !matcher.matches();
		} else {
			return true;
		}
	}

	/**
	 * 检测通过就报false
	 * @param str待检测的字符串
	 * @return 检测不通过就报true
	 */
	public static boolean Long(String str) {
		Long num;
		try {
			if(str.length() == 0 || str.isEmpty() || str.equals("")) {
				return true;
			} 
			num = Long.parseLong(str);
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	/**
	 * 检测通过就报false
	 * @param str待检测的字符串
	 * @return 检测不通过就报true
	 */
	public static boolean Integer(String str) {
		Integer integer;
		try {
			if(str.length() == 0 || str.isEmpty() || str.equals("")) {
				return true;
			} 
			integer = Integer.parseInt(str);
			return false;
		} catch (Exception e) {
			return true;
		}
	}
	
	/**
	 * 检测通过就报false
	 * @param str待检测的字符串
	 * @return 检测不通过就报true
	 */
	public static boolean isInt(String str) {
		Integer integer;
		try {
			if(str.length() == 0 || str.isEmpty() || str.equals("")) {
				return false;
			} 
			integer = Integer.parseInt(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 判断字符串是否是timestamp类型的
	 * @param time
	 * @return 检测不通过就报false
	 */
	public static boolean isTimestamp(String time) {
		try {
			Timestamp.valueOf(time);
			return  true;
		} catch(Exception e) {
			return false;
		}
	} 

	/**
	 * 判断字符串是否是date类型的数据
	 * @param date
	 * @return 检测不通过就报true
	 */
	public static boolean DateSQL(String date) {
		try {
			Date.valueOf(date);
			return false;
		} catch(Exception e) {
			return true;
		}
		/*if(format == null) {
			format = new SimpleDateFormat("yyyy-MM-dd");
		}
		try {
			java.util.Date date1 = format.parse(date);
			System.out.println(date1);
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		}*/
	}

	/**
	 * 检测通过就报false
	 * @param str待检测的字符串
	 * @return 检测不通过就报true
	 */
	public static boolean Double(String str) {
		Double dou;
		try {
			if(str.length() == 0 || str.isEmpty() || str.equals("")) {
				return true;
			} 
			dou = Double.parseDouble(str);
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	/**
	 * 检测通过就报false
	 * @param str待检测的字符串
	 * @return 检测不通过就报true
	 */
	public static boolean Float(String str) {
		Float flo;
		try {
			if(str.length() == 0 || str.isEmpty() || str.equals("")) {
				return true;
			} 
			flo = Float.parseFloat(str);
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	/**
	 * 检测String是否为空
	 * @param str待检测的字符串
	 * @return 检测不通过就报true
	 */
	public static boolean isString(String str) {
		try {
			if(str.length() == 0 || str.isEmpty() || str.equals("")) {
				return false;
			} 
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean Image(String fileFileName) {
		//通过文件流转图片流来判断是否是图片，这样做比较严格
		/*try {
			ImageInputStream imageInputStream = ImageIO.createImageInputStream(file);
			Iterator<ImageReader> iterator = ImageIO.getImageReaders(imageInputStream);
			if(!iterator.hasNext()) {
				return true;
			} else {
				return false;
			}
		} catch(Exception e) {
			return true;
		}*/
		//通过文件名
		System.out.println("name="+fileFileName);
		if(fileFileName.endsWith(".jpg") || fileFileName.endsWith(".jpeg") || fileFileName.endsWith(".png")) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断是否是BigDecimal
	 * @param str
	 * @return
	 */
	public static boolean BigDecimal(String str) {
		try {
			BigDecimal bigDecimal = new BigDecimal(str);
			return false;
		} catch (Exception e) {
			return true;
		}
	}

}
