package com.wx.b360.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//数据字典表
@Table(name="b_datadic")
@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class DataDic {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", nullable=false, columnDefinition="int comment 'id'")
	private int id;
	
	@Column(name="data_Dic_Name", nullable=true, columnDefinition="varchar(255) comment '数据字典名'")
	private String dataDicName;
	
	@Column(name="data_Dic_Value", nullable=true, columnDefinition="varchar(255) comment '数据字典值'")
	private String dataDicValue;
	
	
	public DataDic(String dataDicName, String dataDicValue) {
		this.dataDicName = dataDicName;
		this.dataDicValue = dataDicValue;
	}
	
}
