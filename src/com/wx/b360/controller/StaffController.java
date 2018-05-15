package com.wx.b360.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.wx.b360.entity.Admin;
import com.wx.b360.entity.Source;
import com.wx.b360.entity.Staff;
import com.wx.b360.model.Msg;
import com.wx.b360.tool.CheckTool;
import com.wx.b360.tool.CodeConstant;

//员工
@RestController
@RequestMapping("/staff")
public class StaffController extends BaseController {
	
	//添加员工
	@PostMapping("/add")
	public Msg add(@SessionAttribute Admin admin, @RequestParam String name,
			@RequestParam String duty, @RequestParam String phone, @RequestParam int sourceId) {
		if(!CheckTool.isPhone(phone)) {
			msg.set("手机号格式有误", CodeConstant.ERR_PAR, null);
			return msg;
		}
		Source source = sourceRepository.findOne(sourceId);
		if(source != null) {
			Staff staff = new Staff(name, phone);
			staff = staffRepository.save(staff);
			//source.getStaffs().add(staff);
			source = sourceRepository.save(source);
			if(source != null) {
				msg.set("添加成功", CodeConstant.SUCCESS, source);
			} else {
				msg.set("添加失败", CodeConstant.SET_ERR, null);
			}
		} else {
			msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}
	
	//修改员工
	@PostMapping("/set")
	public Msg set(@SessionAttribute Admin admin, @RequestParam int id, 
			@RequestParam(required=false) String name, @RequestParam(required=false) String duty,
			@RequestParam(required=false) String phone) {
		Staff staff = staffRepository.findOne(id);
		if(staff != null) {
			boolean isChange = false;
			if(CheckTool.isString(name) && !staff.getName().equals(name)) {
				isChange = true;
				staff.setName(name);
			}
			if(CheckTool.isPhone(phone)&& !staff.getPhone().equals(phone)) {
				isChange = true;
				staff.setPhone(phone);
			}
			if(isChange) {
				staff = staffRepository.save(staff);
				msg.set("修改成功", CodeConstant.SUCCESS, staff);
			} else {
				msg.set("没有要修改的数据", CodeConstant.ERROR, null);
			}
		} else {
			msg.set("未找到此条信息", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}
	
	//删除员工
	@PostMapping("/del")
	public Msg del(@SessionAttribute Admin admin, @RequestParam int id) {
		Staff staff = staffRepository.findOne(id);
		if(staff != null) {
			staffRepository.delete(staff);
			msg.set("删除成功", CodeConstant.SUCCESS, null);
		} else {
			msg.set("未找到此条信息", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}
}
