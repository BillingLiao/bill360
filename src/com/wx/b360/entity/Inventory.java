package com.wx.b360.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//持票库存表
@Table(name="b_inventory")
@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Inventory {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="_id", nullable=false, columnDefinition="int comment '持票单id'")
	private int id;
	
	@ManyToOne(targetEntity=User.class)
	@JoinColumn(name="_user_id", nullable=true, columnDefinition="int comment '用户id'")
	private User user;
	
	@ManyToOne(targetEntity=UserByAdmin.class)
	@JoinColumn(name="_userbyadmin_id", nullable=true, columnDefinition="int comment '管理员建用户id'")
	private UserByAdmin userByAdmin;
	
	@Column(name="_company", nullable=false, columnDefinition="varchar(255) comment '开票企业名称'")
	private String company;
	
	@Column(name="_img_front", nullable=false, columnDefinition="varchar(255) comment '商票照片正面'")
	private String img_front;
	
	@Column(name="_img_back", nullable=true, columnDefinition="varchar(255) comment '背书面照片'")
	private String img_back;
	
	@Column(name="_money", nullable=false, columnDefinition="DECIMAL(18,6) default '0.00' comment '金额（万）'")
	private BigDecimal money;
	
	@Column(name="_ctime", nullable=false, columnDefinition="varchar(255) comment '出票日期'")
	private String ctime;
	
	@Column(name="_dtime", nullable=false, columnDefinition="varchar(255) comment '到票日期'")
	private String dtime;
	
	@Column(name="_ttime", nullable=true, columnDefinition="varchar(255) comment '贴现日'")
	private String ttime;
	
	@Column(name="_offer", nullable=true, columnDefinition="varchar(255) comment '接受价额'")
	private String offer;

	public Inventory(UserByAdmin userByAdmin, String company, String img_front, String img_back, BigDecimal money, String ctime,
			String dtime, String ttime, String offer) {
		super();
		this.userByAdmin = userByAdmin;
		this.company = company;
		this.img_front = img_front;
		this.img_back = img_back;
		this.money = money;
		this.ctime = ctime;
		this.dtime = dtime;
		this.ttime = ttime;
		this.offer = offer;
	}
	
	public Inventory(User user, String company, String img_front, String img_back, BigDecimal money, String ctime,
			String dtime, String ttime, String offer) {
		super();
		this.user = user;
		this.company = company;
		this.img_front = img_front;
		this.img_back = img_back;
		this.money = money;
		this.ctime = ctime;
		this.dtime = dtime;
		this.ttime = ttime;
		this.offer = offer;
	}

}
