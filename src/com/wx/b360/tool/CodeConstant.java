package com.wx.b360.tool;

public class CodeConstant {
	
	/**
	 * 成功
	 */
	public static final int SUCCESS = 0;
	
	/**
	 * 通用错误,一般可以直接显示出来
	 */
	public static final int ERROR = 1;
	
	/**
	 * 参数错误
	 */
	public static final int ERR_PAR = 2;
	
	/**
	 * 请求没有授权,一般是没有登录或者token过期
	 */
	public static final int ERR_AUTH = 3;
	
	/**
	 * 未找到数据
	 */
	public static final int FIND_ERR = 4;
	
	/**
	 * 更新失败
	 */
	public static final int SET_ERR = 5;
	
	/**
	 * 此用户已被锁定的情况
	 */
	public static final int LOCK = 6;
	
	/**
	 * 匹配错误--主要是密码错误
	 */
	public static final int MAT_ERR = 7;
	
	/**
	 * 无权操作
	 */
	public static final int NO_AUTH = 8;
	
	/**
	 * 成功(特殊状态)
	 */
	public static final int SUCCESS_2 = 9;
}