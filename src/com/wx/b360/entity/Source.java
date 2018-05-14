package com.wx.b360.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//渠道表
@Table(name="b_source", indexes= {@Index(name="_invoice_index", columnList="_invoice", unique=true)})
@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Source {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="_id", nullable=false, columnDefinition="int comment 'id'")
	private int id;
	
	@Column(name="_invoice", nullable=false, columnDefinition="varchar(255) comment '收票企业'")
	private String invoice;
	
	@Column(name="_rebate", nullable=true, columnDefinition="varchar(255) comment '返佣'")
	private String rebate;
	
	@Column(name="_endorse_name", nullable=true, columnDefinition="varchar(255) comment '背书公司名'")
	private String ename;
	
	@Column(name="_endorse_account", nullable=true, columnDefinition="varchar(255) comment '背书账号'")
	private String eaccount;
	
	@Column(name="_is_bargain", nullable=true, columnDefinition="integer comment '合同0-不是、1-是'")
	private Integer isBargain;
	
	@Column(name="_is_invoice", nullable=true, columnDefinition="integer comment '发票0-不是（默认）、1-是'")
	private Integer isInvoice;
	
	@Column(name="_endorse_time", nullable=true, columnDefinition="int comment '背书次数'")
	private Integer etime;
	
	@Column(name="_remit", nullable=true, columnDefinition="varchar(255) comment '打款时间'")
	private String remit;
	
	@Column(name="_nature", nullable=true, columnDefinition="varchar(255) comment '资金性质'")
	private String nature;
	
	@Column(name="_flow", nullable=true, columnDefinition="varchar(255) comment '流程'")
	private String flow;
	
	@Column(name="_rate", nullable=false, columnDefinition="decimal default '0.0000' comment '利率'")
	private BigDecimal rate;
	
	@Column(name="_level", nullable=false, columnDefinition="int default 0 comment '优先级0-默认'")
	private Integer level;
	
	@Column(name="_adjuest", nullable=false, columnDefinition="int comment '调整天数'")
	private Integer adjuest;
	
	@Cascade(value={org.hibernate.annotations.CascadeType.REMOVE})
	@OneToMany(targetEntity=Staff.class, fetch=FetchType.EAGER)
	@JoinColumn(name="_source_staff")
	private List<Staff> staffs = new ArrayList<>();
	
	public Source(String invoice, String rebate, String eaccount, String ename, Integer isBargain, Integer isInvoice, 
			Integer etime, String remit, String nature, String flow, BigDecimal rate, Integer adjuest) {
		this.invoice = invoice;	this.rebate = rebate;	this.eaccount = eaccount;	this.ename = ename;	
		this.isBargain = isBargain;	this.isInvoice = isInvoice;	this.etime = etime;	this.remit = remit;
		this.nature = nature;	this.flow = flow;	this.rate = rate; this.adjuest = adjuest;
		
	}
}
