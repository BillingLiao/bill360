package com.wx.b360.model;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码
 * @author 莫小雨
 *
 */
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Code {
	private String content;
	private Timestamp time;
	private String phone;
}
