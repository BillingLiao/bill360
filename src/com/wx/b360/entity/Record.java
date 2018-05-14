package com.wx.b360.entity;

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

@Table(name="b_record")
@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Record {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="_id", nullable=false, columnDefinition="int comment 'id'")
	private int id;
	
	@ManyToOne(targetEntity=User.class)
	@JoinColumn(name="_user_id", nullable=false, columnDefinition="int comment '用户id'")
	private User user;
	
	@Column(name="_word", nullable=false, columnDefinition="varchar(255) comment '查询关键字'")
	private String word;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="_time", nullable=false, columnDefinition="timestamp default current_timestamp comment '添加时间'")
	private Date time;
	
	public Record(User user, String word, Date time) {
		this.user = user;
		this.word = word;
	}
	public Record(int id, String word) {
		this.word = word;
		this.id = id;
	}
}
