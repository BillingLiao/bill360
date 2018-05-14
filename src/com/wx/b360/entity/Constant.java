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

@Table(name="b_constant")
@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Constant {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="_id", nullable=false, columnDefinition="int comment 'id'")
	private int id;
	
	@Column(name="_name", columnDefinition="varchar(255) comment '常量名'", nullable=false)
	private String name;
	@Column(name="_value", columnDefinition="varchar(255) comment '常量值'", nullable=false)
	private String value;
	@Column(name="_remark", columnDefinition="varchar(255) comment '常量名'", nullable=true)
	private String remark;
}
