package com.wx.b360.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.wx.b360.entity.Admin;
import com.wx.b360.entity.Constant;
import com.wx.b360.model.Msg;
import com.wx.b360.tool.AppTool;
import com.wx.b360.tool.CheckTool;
import com.wx.b360.tool.CodeConstant;

@RestController
@RequestMapping("/constant")
public class ConstantController extends BaseController {
	
	@PostMapping("/set")
	public Msg set(@SessionAttribute Admin admin, @RequestParam int id, @RequestParam(required=false) String value,
			@RequestParam(required=false) String remark) {
		Constant constant = constantRepository.findOne(id);
		if(constant != null) {
			boolean isChange = false;
			if(CheckTool.isString(value) && !constant.getValue().equals(value)) {
				isChange = true;
				constant.setValue(value);
			}
			if(CheckTool.isString(remark) && !constant.getRemark().equals(remark)) {
				isChange = true;
				constant.setRemark(remark);
			}
			if(isChange) {
				constant = constantRepository.save(constant);
				msg.set("修改成功", CodeConstant.SUCCESS, constant);
			} else {
				msg.set("没有要修改的数据", CodeConstant.SET_ERR, null);
			}
		} else {
			msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}
	
	@PostMapping("/find")
	public Msg find(@SessionAttribute Admin admin, @RequestParam int index, @RequestParam int size) {
		Page<Constant> page = constantRepository.findAll(new PageRequest(index, size, Sort.Direction.DESC, "id"));
		msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(page));
		return msg;
	}
	
	@RequestMapping("/test")
	public Msg test() {
		msg.set("成功", CodeConstant.SUCCESS, this.getClass().getName());
		return msg;
	}
}
