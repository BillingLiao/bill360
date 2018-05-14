package com.wx.b360.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.wx.b360.entity.Acceptance;
import com.wx.b360.entity.Admin;
import com.wx.b360.entity.Source;
import com.wx.b360.entity.Staff;
import com.wx.b360.model.Msg;
import com.wx.b360.tool.AppTool;
import com.wx.b360.tool.CheckTool;
import com.wx.b360.tool.CodeConstant;

import sun.nio.cs.ext.ISO2022_CN;

/**
 * 承兑企业 controller
 * @author Billing
 *
 */
@RestController
@RequestMapping("/acceptance")
public class AcceptanceController extends BaseController{
	
		//删除承兑企业
		@PostMapping("/del")
		public Msg del(@SessionAttribute Admin admin, @RequestParam int id) {
			Acceptance acceptance = acceptanceRepository.findOne(id);
			if(acceptance != null) {
				acceptanceRepository.delete(acceptance);
				msg.set("删除成功", CodeConstant.SUCCESS, null);
			} else {
				msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
			}
			return msg;
		}
		
		//修改承兑企业
		@PostMapping("/set")
		public Msg set(@SessionAttribute Admin admin, @RequestParam int id, 
				@RequestParam(required=false) String invoice, @RequestParam(required=false) String core, 
				@RequestParam(required=false) String category, @RequestParam(required=false) String nature,
				@RequestParam(required=false) Integer type,@RequestParam(required=false) String addr) {
			Acceptance acceptance = acceptanceRepository.findOne(id);
			if(acceptance != null) {
				boolean isChange = false;
				if(!acceptance.getInvoice().equals(invoice)) {  //判断与修改前数据是否不一致
					isChange = true;
					acceptance.setInvoice(invoice);
				}
				if(CheckTool.isString(core) && (acceptance.getCore() == null || !acceptance.getCore().equals(core))) {
					isChange = true;
					acceptance.setCore(core);
				}
				if(CheckTool.isString(category) && (acceptance.getCategory() == null || !acceptance.getCategory().equals(category))) {
					isChange = true;
					acceptance.setCategory(category);
				}
				if(CheckTool.isString(nature) && (acceptance.getNature() == null || !acceptance.getNature().equals(nature))) {
					isChange = true;
					acceptance.setNature(nature);
				}
				if(acceptance.getType() != type.intValue()) {
					isChange = true;
					acceptance.setType(type);
				}
				if(CheckTool.isString(addr) && (acceptance.getAddr() == null || !acceptance.getAddr().equals(addr))) {
					isChange = true;
					acceptance.setAddr(addr);
				}
				
				if(isChange) {
					//修改后资料完善为1
					acceptance.setIs_finish(1);
					
					acceptance = acceptanceRepository.save(acceptance);
					if(acceptance != null) {
						msg.set("修改成功", CodeConstant.SUCCESS, acceptance);	
					}else {
						msg.set("修改失败", CodeConstant.SET_ERR, null);
					}
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
			Acceptance acceptance = acceptanceRepository.findOne(id);
			if(acceptance != null) {
				msg.set("查询成功", CodeConstant.SUCCESS, acceptance);
			} else {
				msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
			}
			return msg;
		}
		
		//获取收票渠道列表
		@PostMapping("/find")
		public Msg find(@RequestParam int index, @RequestParam int size, 
				@RequestParam(required=false) String invoice, @RequestParam(required=false) String core,
				@RequestParam(required=false) Integer is_finish) {
			Page<Acceptance> page = acceptanceService.find(index, size, invoice, core, is_finish);
			msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(page));
			return msg;
		}
		
		//添加收票渠道
		@PostMapping("/add")
		public Msg add(@SessionAttribute Admin admin, @RequestParam String invoice, @RequestParam(required=false) String core, 
				@RequestParam(required=false) String category, @RequestParam(required=false) String nature,
				@RequestParam(required=false) int type,@RequestParam(required=false) String addr) {
			
			Acceptance acceptance = acceptanceRepository.findByInvoice(invoice);
			if(acceptance == null) {
				acceptance = new Acceptance(invoice, core, category, nature, type, addr, 1);

				acceptance = acceptanceRepository.save(acceptance);
				msg.set("添加成功", CodeConstant.SUCCESS, acceptance);
			} else {
				msg.set("承兑企业名被占用", CodeConstant.ERROR, null);
			}
			
			return msg;
		}
		
		@RequestMapping("/test")
		public Msg test() {
			msg.set("成功", CodeConstant.SUCCESS, this.getClass().getName());
			return msg;
		}

}
