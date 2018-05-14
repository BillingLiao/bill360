package com.wx.b360.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="b_admin",indexes= {@Index(name="name_index", columnList="_name", unique=true)})
@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Admin {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="_id", nullable=false, columnDefinition="int comment 'id'")
	private int id;
	
	@Column(name="_name", nullable=false, columnDefinition="varchar(255) comment '登录名'")
	private String name;
	
	@Column(name="_pwd", nullable=false, columnDefinition="varchar(255) comment '登录密码'")
	private String pwd;
	
	public Admin(String name, String pwd) {
		this.name = name;
		this.pwd = pwd;
	}
}
