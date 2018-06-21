package com.wx.b360.controller;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.wx.b360.entity.DataDic;
import com.wx.b360.entity.Record;
import com.wx.b360.entity.User;
import com.wx.b360.model.Msg;
import com.wx.b360.tool.AppTool;
import com.wx.b360.tool.CodeConstant;

@RestController
@RequestMapping("/record")
public class RecordController extends BaseController {
	
	//获取热门搜索
	@PostMapping("/hot")
	public Msg hot() {
		String dataDicName = "热门搜索";
		List<String>  dataDicValueList  = dataDicRepository.findDataDicValueLimitByName(dataDicName);
		msg.set("查询成功", CodeConstant.SUCCESS, dataDicValueList);
		//msg.set("查询成功", CodeConstant.SUCCESS, recordService.rank());
		return msg;
	}
	
	//获取当前用户的历史搜索
	@PostMapping("/my")
	public Msg my(@SessionAttribute User user, @RequestParam int size) {
		Page<Record> page = recordService.find(0, size, user, null);
		msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(page));
		return msg;
	}
	@RequestMapping("/test")
	public Msg test() {
		msg.set("成功", CodeConstant.SUCCESS, this.getClass().getName());
		return msg;
	}
}
