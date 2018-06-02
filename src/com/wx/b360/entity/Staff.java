package com.wx.b360.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//员工表
@Table(name="b_staff")
@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Staff {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="_id", nullable=false, columnDefinition="int comment 'id'")
	private int id;
	
	@Column(name="_name", nullable=false, columnDefinition="varchar(255) comment '对接人姓名'")
	private String name;
	
	@Column(name="_company", nullable=false, columnDefinition="varchar(255) comment '公司'")
	private String company;
	
	@Column(name="_endorse_company", nullable=true, columnDefinition="varchar(255) comment '背书公司名'")
	private String eCompany;
	
	@Column(name="_endorse_account", nullable=true, columnDefinition="varchar(255) comment '背书账号'")
	private String eAccount;
	
	@Column(name="_phone", nullable=true, columnDefinition="varchar(255) comment '联系电话'")
	private String phone;
	
	@Column(name="_wechat", nullable=true, columnDefinition="varchar(255) comment '微信id'")
	private String wechat;
		
	@Column(name="_addr", nullable=true, columnDefinition="varchar(255) comment '地址'")
	private String addr;
	
	@Column(name="_area", nullable=true, columnDefinition="varchar(255) comment '地区'")
	private String area;
	
	@Column(name="_business_license", nullable=true, columnDefinition="varchar(255) comment '营业执照号'")
	private String bLicense;
	
	@Column(name="_business_license_photo", nullable=true, columnDefinition="varchar(255) comment '营业执照（照片）'")
	private String bLPhono;
	
	@Column(name="_legal_person_name", nullable=true, columnDefinition="varchar(255) comment '法人姓名'")
	private String lPName;
	
	@Column(name="_id_card_front", nullable=true, columnDefinition="varchar(255) comment '法人身份证(正面)'")
	private String idCard_front;
	
	@Column(name="_id_card_back", nullable=true, columnDefinition="varchar(255) comment '法人身份证(反面)'")
	private String idCard_back;

	public Staff(String name, String company, String eCompany, String eAccount, String phone,String wechat, String addr, String area) {
		super();
		this.name = name;
		this.company = company;
		this.eCompany = eCompany;
		this.eAccount = eAccount;
		this.phone = phone;
		this.wechat = wechat;
		this.addr = addr;
		this.area = area;
	}
	
}
