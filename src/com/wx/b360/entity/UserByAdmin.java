package com.wx.b360.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.wx.b360.tool.CheckTool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//管理员建立的用户表
@Table(name="b_userbyadmin")
@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class UserByAdmin {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", nullable=false, columnDefinition="int comment 'id'")
	private int id;
	
	@Column(name="name", nullable=false, columnDefinition="varchar(255) comment '姓名'")
	private String name;
	
	@Column(name="addr", nullable=true, columnDefinition="varchar(255) comment '地址'")
	private String addr;
	
	@Column(name="company", nullable=true, columnDefinition="varchar(255) comment '公司名'")
	private String company;

	@Column(name="phone", nullable=true, columnDefinition="varchar(13) comment '手机'")
	private String phone;
	
	@Column(name="wechat", nullable=true, columnDefinition="varchar(13) comment '手机'")
	private String wechat;
	
	
	public UserByAdmin(String addr, String company, String name, String phone, String wechat) {
		this.name = name;
		if(CheckTool.isString(addr)) this.addr = addr;
		if(CheckTool.isString(company)) this.company = company;
		if(CheckTool.isPhone(phone)) this.phone = phone;
		if(CheckTool.isString(wechat)) this.wechat = wechat;
	}
}
