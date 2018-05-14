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
//票据表
@Table(name="b_bill")
@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Bill {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="_id", nullable=false, columnDefinition="int comment 'id'")
	private int id;
	
	@ManyToOne(targetEntity=Acceptance.class)
	@JoinColumn(name="_acceptance_id", nullable=false, columnDefinition="int comment '承兑企业id'")
	private Acceptance acceptance;
	
	@ManyToOne(targetEntity=Acceptance.class)
	@JoinColumn(name="_staff_id", nullable=false, columnDefinition="int comment '对接人id'")
	private Staff staff;
	
	@Column(name="_rate", nullable=false, columnDefinition="DECIMAL(6,2) default '0.00' comment '利率'")
	private BigDecimal rate;
	
	@Column(name="_shortest", nullable=false, columnDefinition="int comment '最短期限'")
	private int shortest;
	
	@Column(name="_adjuest", nullable=false, columnDefinition="int comment '调整天数'")
	private int adjuest;
	
	@Column(name="_initiate", nullable=false,columnDefinition="DECIMAL(14,2) default '0.00' comment '起步金额'")
	private BigDecimal initiate;
	
	@Column(name="_max", nullable=false, columnDefinition="DECIMAL(14,2) default '0.00' comment '最大金额'")
	private BigDecimal max;
	
	@Column(name="_min", nullable=false, columnDefinition="DECIMAL(14,2) default '0.00' comment '最小金额'")
	private BigDecimal min;
	
	@Column(name="_total", nullable=false, columnDefinition="DECIMAL(14,2) default '0.00' comment '总额度'")
	private BigDecimal total;
	
	@Column(name="_usable", nullable=false, columnDefinition="DECIMAL(14,2) default '0.00' comment '可用额度'")
	private BigDecimal usable;
	
	
	@Column(name="_remark", nullable=true, columnDefinition="varchar(255) comment '保理备注'")
	private String remark;
	
	@Column(name="_status", nullable=false, columnDefinition="int default 0 comment '状态0-正常（默认）、1-已关闭'")
	private int status;
	
	@Column(name="_level", nullable=false, columnDefinition="int default 0 comment '优先级0-默认'")
	private int level;
	
	@Column(name="_type", nullable=false, columnDefinition="int default 0 comment '类型0-母公司、1-子公司、2-孙公司'")
	private int type;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="_ctime", nullable=false, columnDefinition="timestamp default current_timestamp comment '添加时间'")
	private Date ctime;
	
	public Bill(BigDecimal rate, BigDecimal initiate, BigDecimal max,
			BigDecimal min, BigDecimal total, BigDecimal usable, int shortest,int adjuest ,
			String remark, int level,int type) {
		this.rate = rate;	this.initiate = initiate;
		this.max = max;	this.min = min; this.total = total;	this.usable = usable;
		this.shortest = shortest; this.adjuest = adjuest; this.remark = remark; this.level = level; this.type = type;
	}
	
	public Bill(BigDecimal rate, BigDecimal initiate, BigDecimal max, BigDecimal total, BigDecimal usable,
			int shortest,String remark, int type, BigDecimal min) {
			this.rate = rate;	this.initiate = initiate;
		this.max = max;	this.total = total;	this.usable = usable;	this.shortest = shortest;
		this.remark = remark;	this.type = type;	this.min = min;
	}
}
