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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="b_pwd", indexes= {@Index(name="user_index", columnList="_user_id", unique=true),@Index(name="openid_index", columnList="_openid", unique=true)})
@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Pwd {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="_id", nullable=false, columnDefinition="int comment 'id'")
	private int id;
	
	@OneToOne(targetEntity=User.class)
	@JoinColumn(name="_user_id", nullable=false, columnDefinition="int comment '用户id'")
	private User user;
	
	@Column(name="_openid", nullable=false, columnDefinition="varchar(255) comment '微信openid'")
	private String openid;
	
	@Column(name="_phone", nullable=true, columnDefinition="varchar(13) comment '手机号'")
	private String phone;
	
	public Pwd(User user, String openid) {
		this.user = user;
		this.openid = openid;
	}
}
