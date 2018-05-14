package com.wx.b360.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.wx.b360.tool.CheckTool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="b_user")
@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class User {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="_id", nullable=false, columnDefinition="int comment 'id'")
	private int id;
	
	@Column(name="_head", columnDefinition="varchar(255) default 'user-head.png' comment '用户头像'", nullable=false)
	private String head;
	
	@Column(name="_name", columnDefinition="varchar(255) default '查票360' comment '用户名-微信昵称'", nullable=false)
	private String name;
	
	@Column(name="_status", nullable=false, columnDefinition="int default 0 comment '用户状态0-正常（默认）、1-管理员锁定'")
	private int status;
	
	@Column(name="_type", nullable=false, columnDefinition="int default 0 comment '用户类型0-未绑定手机、1-已绑定手机'")
	private int type;
	
	@Column(name="_card", nullable=false, columnDefinition="int default 0 comment '是否上传了名片0-未上传、1-已上传'")
	private int card;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="_reg", nullable=false, columnDefinition="timestamp default current_timestamp comment '注册时间'")
	private Date reg;
	
	@Column(name="_precise", nullable=false, columnDefinition="int default 0 comment '精确搜索次数'")
	private int precise;
	@Transient
	private String phone;
	
	public User(String head, String name) {
		this.head = head;	
		if(CheckTool.isString(name))this.name = name;
	}
}
