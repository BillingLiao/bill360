package com.wx.b360.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class Staff_import {

	private String name; //姓名

	private String company; //公司名
	
	private String eCompany; //背书公司名
	
	private String eAccount; //背书账号
	
	private String phone; //电话
	
	private String wechat; //微信号
		
	private String addr; //地址
	
	private String area; //地区

	public Staff_import() {
		super();
	}

	@Override
	public String toString() {
		return "Staff_import [name=" + name + ", company=" + company + ", eCompany=" + eCompany + ", eAccount="
				+ eAccount + ", phone=" + phone + ", wechat=" + wechat + ", addr=" + addr + ", area=" + area + "]";
	}
	
	
}
