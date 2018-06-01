package com.wx.b360.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.wx.b360.entity.Acceptance;
import com.wx.b360.entity.Staff;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 收票清单——excel导入临时类
 * @author Billing
 *
 */
@Data @AllArgsConstructor
public class bill_import {

	private Acceptance acceptance;

	private Staff staff;

	private BigDecimal rate; //利率

	private int shortest; //收票起步天数

	private int longest; //收票最高天数

	private int adjuest; //调整天数

	private String max; //收票单张上限金额(万)

	private BigDecimal min; //收票单张起步金额(万)

	private BigDecimal total; //总额度(万)

	private BigDecimal usable; //可用额度(万)

	private String remark;	//备注

	private int status; //状态

	private int level; //优先级

	private Date ctime; //创建时间

	private int isBargain; //合同

	private int isInvoice; //发票

	private int agreement; //质押协议

	private int isMoneyOrBack; //先款先背

	private int isFinancing; //融资票

	private int isClean; //光票

	private int etime; //背书次数限制

	private Integer offer; //报价方式

	private BigDecimal deductions; //每十万扣费
	
	private BigDecimal direct; //直接扣费(%)
}
