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

@Table(name="b_order")
@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Order {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="_id", nullable=false, columnDefinition="int comment 'id'")
	private int id;
	
	@ManyToOne(targetEntity=Bill.class)
	@JoinColumn(name="_bill_id", nullable=false, columnDefinition="int comment '票据id'")
	private Bill bill;
	
	@ManyToOne(targetEntity=Source.class)
	@JoinColumn(name="_source_id", nullable=false, columnDefinition="int comment '渠道id'")
	private Source source;
	
	@Column(name="_invoice", columnDefinition="varchar(255) comment '开票企业'", nullable=false)
	private String invoice;
	
	@Column(name="_core", columnDefinition="varchar(255) comment '核心企业'", nullable=false)
	private String core;
	
	@Column(name="_money", nullable=false, columnDefinition="DECIMAL(14,2) default '0.00' comment '金额'")
	private BigDecimal money; 
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="_time", nullable=false, columnDefinition="timestamp default current_timestamp comment '到期时间'")
	private Date time;
	
	@Column(name="_subsidy", nullable=false, columnDefinition="DECIMAL(14,2) default '0.00' comment '保理金额'")
	private BigDecimal subsidy;
	
	@Column(name="_interest", nullable=false, columnDefinition="DECIMAL(14,2) default '0.00' comment '保理利息'")
	private BigDecimal interest;
	
	@Column(name="_day", nullable=false, columnDefinition="int default 0 comment '计息天数'")
	private int day;
	
	@Column(name="_img", nullable=false, columnDefinition="varchar(255) comment '商票照片'")
	private String img;
	
	@Column(name="_name", nullable=false, columnDefinition="varchar(255) comment '联系人名称'")
	private String name;
	
	@Column(name="_phone", nullable=false, columnDefinition="varchar(255) comment '收票渠道-手机号'")
	private String phone;
	
	@Column(name="_status", nullable=false, columnDefinition="int default 0 comment '状态0-未处理、1-处理中、2-已处理'")
	private int status;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="_ctime", nullable=false, columnDefinition="timestamp default current_timestamp comment '创建时间'")
	private Date ctime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="_stime", nullable=false, columnDefinition="timestamp default current_timestamp comment '处理时间（如果未处理则和创建时间一致）'")
	private Date stime;
	@ManyToOne(targetEntity=User.class)
	@JoinColumn(name="_user_id", nullable=false, columnDefinition="int comment '用户id'")
	private User user;
	
	public Order(Bill bill, Source source, String invoice, String core, BigDecimal money,
			Date time, BigDecimal subsidy, BigDecimal interest, int day, String img, String name,
			String phone, User user) {
		this.bill = bill;	this.source = source;	this.invoice = invoice;	this.core = core;
		this.money = money;	this.time = time;	this.subsidy = subsidy;	this.interest = interest;
		this.day = day;	this.img = img;	this.name = name;	this.phone = phone;	this.user = user;
		
	}
	
}
