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

//名片表
@Table(name="b_card", indexes= {@Index(name="user_index", columnList="_user_id", unique=true)})
@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Card {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="_id", nullable=false, columnDefinition="int comment 'id'")
	private int id;
	
	@OneToOne(targetEntity=User.class)
	@JoinColumn(name="_user_id", nullable=false, columnDefinition="int comment '用户id'")
	private User user;
	
	@Column(name="_name", nullable=false, columnDefinition="varchar(255) comment '名称'")
	private String name;
	
	@Column(name="_addr", nullable=true, columnDefinition="varchar(255) comment '地址'")
	private String addr;
	
	@Column(name="_company", nullable=true, columnDefinition="varchar(255) comment '公司名'")
	private String company;
	
	@Column(name="_tel", nullable=true, columnDefinition="varchar(20) comment '固话'")
	private String tel;
	
	@Column(name="_phone", nullable=true, columnDefinition="varchar(13) comment '手机'")
	private String phone;

	public Card(User user, String name, String addr, String company, String tel, String phone) {
		this.user = user;
		this.name = name;
		if(CheckTool.isString(addr)) this.addr = addr;
		if(CheckTool.isString(company)) this.company = company;
		if(CheckTool.isString(tel)) this.tel = tel;
		if(CheckTool.isPhone(phone)) this.phone = phone;
	}
}
