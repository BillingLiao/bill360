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
@Table(name = "b_bill")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "_id", nullable = false, columnDefinition = "int comment 'id'")
	private int id;

	@ManyToOne(targetEntity = Acceptance.class)
	@JoinColumn(name = "_acceptance_id", nullable = false, columnDefinition = "int comment '承兑企业id'")
	private Acceptance acceptance;

	@ManyToOne(targetEntity = Staff.class)
	@JoinColumn(name = "_staff_id", nullable = false, columnDefinition = "int comment '对接人id'")
	private Staff staff;

	@Column(name = "_rate", nullable = true, columnDefinition = "DECIMAL(6,2) default '0.00' comment '利率'")
	private BigDecimal rate;

	@Column(name = "_shortest", nullable = true, columnDefinition = "varchar(255) comment '最短期限(收票起步天数)'")
	private String shortest;

	@Column(name = "_longest", nullable = true, columnDefinition = "varchar(255) comment '最长期限(收票最高天数)'")
	private String longest;

	@Column(name = "_adjuest", nullable = true, columnDefinition = "int comment '调整天数'")
	private int adjuest;

	@Column(name = "_max", nullable = true, columnDefinition = "varchar(255) comment '票面最大金额(收票单张上限金额(万))'")
	private String max;

	@Column(name = "_min", nullable = true, columnDefinition = "varchar(255) comment '最小金额(收票单张起步金额(万))'")
	private String min;

	@Column(name = "_total", nullable = true, columnDefinition = "varchar(255) comment '总额度(万)'")
	private String total;

	@Column(name = "_usable", nullable = true, columnDefinition = "varchar(255) comment '可用额度(万)'")
	private String usable;

	@Column(name = "_remark", nullable = true, columnDefinition = "varchar(255) comment '保理备注'")
	private String remark;

	@Column(name = "_status", nullable = false, columnDefinition = "int default 0 comment '状态0-正常（默认）、1-已关闭'")
	private int status;

	@Column(name = "_level", nullable = true, columnDefinition = "int default 0 comment '优先级0-默认'")
	private int level;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "_ctime", nullable = false, columnDefinition = "timestamp default current_timestamp comment '添加时间'")
	private Date ctime;

	@Column(name = "_is_bargain", nullable = true, columnDefinition = "int comment '合同0-不是、1-是'")
	private Integer isBargain;

	@Column(name = "_is_invoice", nullable = true, columnDefinition = "int comment '发票0-不是（默认）、1-是'")
	private Integer isInvoice;

	@Column(name = "_agreement", nullable = true, columnDefinition = "int comment '质押协议0-不是（默认）、1-是'")
	private Integer agreement;

	@Column(name = "_is_moneyorback", nullable = true, columnDefinition = "int comment '先款先背0-先款（默认）、1-先背'")
	private Integer isMoneyOrBack;

	@Column(name = "_is_financing", nullable = true, columnDefinition = "int comment '融资票0-不是（默认）、1-是'")
	private Integer isFinancing;

	@Column(name = "_is_clean", nullable = true, columnDefinition = "int comment '融资票0-不是（默认）、1-是'")
	private Integer isClean;

	@Column(name = "_endorse_time", nullable = true, columnDefinition = "varchar(255) comment '背书次数限制'")
	private String etime;

	@Column(name = "_offer", nullable = true, columnDefinition = "int comment '报价方式'")
	private Integer offer;

	@Column(name = "_deductions", nullable = true, columnDefinition = "DECIMAL(14,2) default '0.00' comment '每十万扣费'")
	private BigDecimal deductions;
	
	@Column(name = "_direct", nullable = true, columnDefinition = "DECIMAL(14,2) default '0.00' comment '直接扣？%'")
	private BigDecimal direct;
	
	@Column(name = "_a_year_interest", nullable = true, columnDefinition = "DECIMAL(16,4) default '0.00' comment '一年每十万利息'")
	private BigDecimal aYInterest;

	public Bill(Acceptance acceptance, Staff staff, BigDecimal rate, String shortest, String longest,Integer adjuest, BigDecimal deductions,
			BigDecimal direct, String max, String min, String total, String usable, String remark, int status, int level,
			int isBargain, int isInvoice, int agreement, int isMoneyOrBack, int isFinancing, int isClean, String etime, Integer offer) {
		super();
		this.acceptance = acceptance;
		this.staff = staff;
		this.rate = rate;
		this.shortest = shortest;
		this.longest = longest;
		this.adjuest = adjuest;
		this.deductions = deductions;
		this.direct = direct;
		this.max = max;
		this.min = min;
		this.total = total;
		this.usable = usable;
		this.remark = remark;
		this.status = status;
		this.level = level;
		this.isBargain = isBargain;
		this.isInvoice = isInvoice;
		this.agreement = agreement;
		this.isMoneyOrBack = isMoneyOrBack;
		this.isFinancing = isFinancing;
		this.isClean = isClean;
		this.etime = etime;
		this.offer = offer;
	}
}
