package com.wx.b360.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.wx.b360.entity.Admin;
import com.wx.b360.entity.Source;
import com.wx.b360.entity.Staff;
import com.wx.b360.model.Msg;
import com.wx.b360.tool.AppTool;
import com.wx.b360.tool.CheckTool;
import com.wx.b360.tool.CodeConstant;

@RestController
@RequestMapping("/source")
public class SourceController extends BaseController {
	
	//删除渠道
	@PostMapping("/del")
	public Msg del(@SessionAttribute Admin admin, @RequestParam int id) {
		Source source = sourceRepository.findOne(id);
		if(source != null) {
			sourceRepository.delete(source);
			msg.set("删除成功", CodeConstant.SUCCESS, null);
		} else {
			msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}
	
	//修改渠道
	@PostMapping("/set")
	public Msg set(@SessionAttribute Admin admin, @RequestParam int id, 
			@RequestParam(required=false) String rebate, @RequestParam(required=false) String ename, 
			@RequestParam(required=false) String eaccount, @RequestParam(required=false) Integer isBargain,
			@RequestParam(required=false) Integer isInvoice, @RequestParam(required=false) Integer etime, 
			@RequestParam(required=false) String remit, @RequestParam(required=false) String nature,	
			@RequestParam(required=false) String flow, @RequestParam(required=false) Integer level) {
		Source source = sourceRepository.findOne(id);
		if(source != null) {
			boolean isChange = false;
			if(!source.getRebate().equals(rebate)) {  //判断与修改前数据是否不一致
				isChange = true;
				source.setRebate(rebate);
			}
			if(!source.getEname().equals(ename)) {
				isChange = true;
				source.setEname(ename);
			}
			if(!source.getEaccount().equals(eaccount)) {
				isChange = true;
				source.setEaccount(eaccount);
			}
			if(source.getIsBargain().intValue() != isBargain.intValue()) {
				isChange = true;
				source.setIsBargain(isBargain);
			}
			if(source.getIsInvoice().intValue() != isInvoice.intValue()) {
				isChange = true;
				source.setIsInvoice(isInvoice);
			}
			if(source.getEtime().intValue() != etime.intValue()) {
				isChange = true;
				source.setEtime(etime);
			}
			if(!source.getRemit().equals(remit)) {
				isChange = true;
				source.setRemit(remit);
			}
			if(!source.getNature().equals(nature)) {
				isChange = true;
				source.setNature(nature);
			}
			if(!source.getFlow().equals(flow)) {
				isChange = true;
				source.setFlow(flow);
			}
			if(source.getLevel().intValue() != level.intValue()) {
				isChange = true;
				source.setLevel(level);
			}
			/*
			 * 当修改为空值时检测不到修改，不能成功提交
			 * 
			 * if(CheckTool.isString(rebate) && (source.getRebate() == null || !source.getRebate().equals(rebate)) ) {
				isChange = true;
				source.setRebate(rebate);
			}
			if(CheckTool.isString(ename) && (source.getEname() == null || !source.getEname().equals(ename))) {
				isChange = true;
				source.setEname(ename);
			}
			if(CheckTool.isString(eaccount) && (source.getEaccount() == null || !source.getEaccount().equals(eaccount))) {
				isChange = true;
				source.setEaccount(eaccount);
			}
			if(isBargain != null && (source.getIsBargain() == null || source.getIsBargain().intValue() != isBargain.intValue())) {
				isChange = true;
				source.setIsBargain(isBargain);
			}
			if(isInvoice != null && (source.getIsInvoice() == null || source.getIsInvoice().intValue() != isInvoice.intValue())) {
				isChange = true;
				source.setIsInvoice(isInvoice);
			}
			if(etime != null && (source.getEtime() == null || source.getEtime().intValue() != etime.intValue())) {
				isChange = true;
				source.setEtime(etime);
			}
			if(CheckTool.isString(remit) && (source.getRemit() == null || !source.getRemit().equals(remit))) {
				isChange = true;
				source.setRemit(remit);
			}
			if(CheckTool.isString(nature) && (source.getNature() == null || !source.getNature().equals(nature))) {
				isChange = true;
				source.setNature(nature);
			}
			if(CheckTool.isString(flow) && (source.getFlow() == null || !source.getFlow().equals(flow))) {
				isChange = true;
				source.setFlow(flow);
			}
			if(level != null && source.getLevel() != level.intValue()) {
				isChange = true;
				source.setLevel(level);
			}
			if(adjuest != null && adjuest.intValue() != source.getAdjuest()) {
				isChange = true;
				source.setAdjuest(adjuest);
			}
			*/
			if(isChange) {
				source = sourceRepository.save(source);
				msg.set("修改成功", CodeConstant.SUCCESS, source);	
			} else {
				msg.set("没有要修改的数据", CodeConstant.ERROR, null);
			}
		} else {
			msg.set("未找到此条信息", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}
	
	//获取指定收票渠道详情
	@PostMapping("/id")
	public Msg findById(@SessionAttribute Admin admin, @RequestParam int id) {
		Source source = sourceRepository.findOne(id);
		if(source != null) {
			msg.set("查询成功", CodeConstant.SUCCESS, source);
		} else {
			msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}
	
	//获取收票渠道列表
	@PostMapping("/find")
	public Msg find(@RequestParam int index, @RequestParam int size, 
			@RequestParam(required=false) Integer level, @RequestParam(required=false) String invoice) {
		Page<Source> page = sourceService.find(index, size, level, invoice);
		msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(page));
		return msg;
	}
	
	//添加收票渠道
	@PostMapping("/add")
	public Msg add(@SessionAttribute Admin admin, @RequestParam String invoice, @RequestParam(required=false) String rebate, 
			@RequestParam(required=false) String ename, @RequestParam(required=false) String eaccount, @RequestParam(required=false) Integer isBargain,
			@RequestParam(required=false) Integer isInvoice, @RequestParam(required=false) Integer etime, @RequestParam String remit, 
			@RequestParam String nature,	@RequestParam String flow,
			@RequestParam(required=false) String staffName, @RequestParam(required=false) String staffDuty,
			@RequestParam(required=false) String staffPhone, @RequestParam BigDecimal rate, @RequestParam int adjuest) {
		
		//收票企业名称唯一
		Source source = sourceRepository.findByInvoice(invoice);
		if(source == null) {
			source = new Source(invoice, rebate, eaccount, ename, isBargain, isInvoice, etime, remit, nature, flow,rate, adjuest);
			source.setLevel(0);
			if(CheckTool.isString(staffName) && CheckTool.isString(staffDuty) && CheckTool.isPhone(staffPhone)) {
				Staff staff = new Staff(staffName, staffPhone);
				staff = staffRepository.save(staff);
				source.getStaffs().add(staff);
			}
			source = sourceRepository.save(source);
			msg.set("添加成功", CodeConstant.SUCCESS, source);
		} else {
			msg.set("收票企业名被占用", CodeConstant.ERROR, null);
		}
		
		return msg;
	}
	
	@RequestMapping("/test")
	public Msg test() {
		msg.set("成功", CodeConstant.SUCCESS, this.getClass().getName());
		return msg;
	}
}
