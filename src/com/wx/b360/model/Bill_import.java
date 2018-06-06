package com.wx.b360.model;

import java.math.BigDecimal;
import java.util.Date;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 收票清单——excel导入临时类
 * @author Billing
 *
 */
@Data @AllArgsConstructor
public class Bill_import {

	private String acceptanceCore; //承兑企业上市主体
	
	private String acceptanceInvoice; //承兑企业名称

	private String staffName;  //对接人姓名
	
	private String staffCompany; //对接人公司
	
	private String staffPhone; //对接人电话

	private BigDecimal rate; //利率

	private String shortest; //收票起步天数

	private String longest; //收票最高天数

	private int adjuest; //调整天数

	private String max; //收票单张上限金额(万)

	private String min; //收票单张起步金额(万)

	private String total; //总额度(万)

	private String usable; //可用额度(万)

	private String remark;	//备注

	private int status; //状态

	private int level; //优先级

	private Date ctime; //创建时间

	private String isBargain; //合同

	private String isInvoice; //发票

	private String agreement; //质押协议

	private String isMoneyOrBack; //先款先背

	private String isFinancing; //融资票

	private String isClean; //光票

	private String etime; //背书次数限制

	private Integer offer; //报价方式

	private BigDecimal deductions; //每十万扣费
	
	private BigDecimal direct; //直接扣费(%)
	
	public Bill_import() {
		super();
	}

	@Override
	public String toString() {
		return "Bill_import [acceptanceCore=" + acceptanceCore + ", acceptanceInvoice=" + acceptanceInvoice
				+ ", staffName=" + staffName + ", staffCompany=" + staffCompany + ", staffPhone=" + staffPhone
				+ ", rate=" + rate + ", shortest=" + shortest + ", longest=" + longest + ", adjuest=" + adjuest
				+ ", max=" + max + ", min=" + min + ", total=" + total + ", usable=" + usable + ", remark=" + remark
				+ ", status=" + status + ", level=" + level + ", ctime=" + ctime + ", isBargain=" + isBargain
				+ ", isInvoice=" + isInvoice + ", agreement=" + agreement + ", isMoneyOrBack=" + isMoneyOrBack
				+ ", isFinancing=" + isFinancing + ", isClean=" + isClean + ", etime=" + etime + ", offer=" + offer
				+ ", deductions=" + deductions + ", direct=" + direct + "]";
	}

}
