package com.wx.b360.model;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class Msg {
	private int code;
	private Object data;
	private String message;
	
	public void set(String message, int CodeConstant, Object data) {
		this.code = CodeConstant;
		this.message = message;
		this.data = data;
	}
}