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

//承兑企业表
@Table(name="b_acceptance")
@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Acceptance {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="_id", nullable=false, columnDefinition="int comment 'id'")
	private int id;
	
	@Column(name="_invoice", nullable=true, columnDefinition="varchar(255) comment '承兑企业名称(开票企业)'")
	private String invoice;
	
	@Column(name="_core", nullable=true, columnDefinition="varchar(255) comment '上市主体(核心)'")
	private String core;
	
	@Column(name="_category", nullable=true, columnDefinition="varchar(255) comment '企业类别'")
	private String category;
	
	@Column(name="_nature", nullable=true, columnDefinition="varchar(255) comment '企业性质'")
	private String nature;
	
	@Column(name="_type", nullable=true, columnDefinition="int default 0 comment '类型0-母公司、1-子公司、2-孙公司'")
	private int type;
	
	@Column(name="_addr", nullable=true, columnDefinition="varchar(255) comment '地址'")
	private String addr;
	
	@Column(name="_area", nullable=true, columnDefinition="varchar(255) comment '地区'")
	private String area;
	
	@Column(name="_is_finish", nullable=false, columnDefinition="int default 0 comment '企业信息0-未完善、1-已完善'")
	private int is_finish;

	public Acceptance(String invoice, String core, String category, String nature, int type, String addr, String area, int is_finish) {
		super();
		this.invoice = invoice;
		this.core = core;
		this.category = category;
		this.nature = nature;
		this.type = type;
		this.addr = addr;
		this.area = area;
		this.is_finish = is_finish;
	}
	
}
