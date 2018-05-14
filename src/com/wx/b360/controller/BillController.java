package com.wx.b360.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.wx.b360.entity.Admin;
import com.wx.b360.entity.Bill;
import com.wx.b360.entity.Record;
import com.wx.b360.entity.Source;
import com.wx.b360.entity.Staff;
import com.wx.b360.entity.User;
import com.wx.b360.model.Company;
import com.wx.b360.model.Msg;
import com.wx.b360.tool.AppTool;
import com.wx.b360.tool.CheckTool;
import com.wx.b360.tool.CodeConstant;
import com.wx.b360.tool.HttpTool;

@RestController
@RequestMapping("/bill")
public class BillController extends BaseController {
	
	//识别票据信息
	@PostMapping("/ocr")
	public Msg ocr(@RequestParam MultipartFile file) {
		
		
		String base64 = null;
		FileInputStream fileInputStream;
		try {
			fileInputStream = (FileInputStream) file.getInputStream();
			byte [] bytes = new byte [fileInputStream.available()]; 
			fileInputStream.read(bytes); 
			base64 = java.util.Base64.getEncoder().encodeToString(bytes);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(CheckTool.isString(base64) && base64.length() > 21) {
			String url = "http://120.76.155.30:9191/pcc-ocr/api/recognize";
			Map<String, String> params = new HashMap<>();
			params.put("image_data", base64);
			String result = HttpTool.post(url, params);
			JSONObject jsonObject = JSONObject.parseObject(result);
			if(jsonObject.getInteger("code").intValue() == 200) {
				String recognize = jsonObject.getString("recognize_data");
				String endDate = null;
				String company = null;
				if(CheckTool.isString(recognize)) {
					jsonObject = JSONObject.parseObject(recognize);
					endDate = jsonObject.getString("endDate");
					company = jsonObject.getString("kpCompany");
					Map<String, String> data = new HashMap<>();
					data.put("endDate", endDate);
					data.put("company", company);
					data.put("ocr-result", result);
					
					msg.set("识别成功", CodeConstant.SUCCESS, data);
				} else {
					msg.set("识别失败:没有识别数据" , CodeConstant.ERROR, null);
				}
						
			} else {
				msg.set("识别失败:" + jsonObject.getString("message"), CodeConstant.ERROR, null);
			}
		} else {
			msg.set("base64字串格式有误", CodeConstant.ERR_PAR, null);
		}
		return msg;
	}
	
	//获取票据和与之关联的收票渠道
	@PostMapping("/id/source")
	public Msg findByIdWithSource(@SessionAttribute Admin admin, @RequestParam int id) {
		Bill bill = billRepository.findOne(id);
		if(bill != null) {

			Map<String, Object> result = new HashMap<>();
			result.put("bill", bill);

			
			msg.set("查询成功", CodeConstant.SUCCESS, result);
		} else {
			msg.set("未找到此条信息", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}
	
	//删除票据
	@PostMapping("/del")
	public Msg del(@SessionAttribute Admin admin, @RequestParam int id) {
		Bill bill = billRepository.findOne(id);
		if(bill != null) {
			billRepository.delete(bill);
			msg.set("删除成功", CodeConstant.SUCCESS, null);
		} else {
			msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}
	//精确搜索
	@PostMapping("/precise")
	public Msg precise(@RequestParam String core, @RequestParam int type, @RequestParam String invoice,
			@RequestParam int index, @RequestParam int size, @SessionAttribute(required=false) User user) {
		if(user.getCard() == 1) {
			Page<Bill> page = billService.find(index, size, 0, core, null, type, invoice);
			msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(page));
		} else if(user.getPrecise() < 5) {
			user.setPrecise(user.getPrecise()+1);
			user = userRepository.save(user);
			//更新缓存
			ValueOperations<String, User> operations=redisTemplate.opsForValue();
			operations.set("user:"+user.getId(), user);
			Page<Bill> page = billService.find(index, size, 0, core, null, type, invoice);
			msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(page));
		} else {
			msg.set("请先上传名片", CodeConstant.ERROR, null);
		}
		return msg;
	}
	
	//企业核心名模糊搜索
	@PostMapping("/core")
	public Msg core(@RequestParam int index, @RequestParam int size, @RequestParam String core) {
		Page<Bill> bills = billService.find(index, size, 0, core, null, null, null);
		List<Bill> billsList = bills.getContent();
		long count = bills.getTotalElements();
		ArrayList<Company> companies = new ArrayList<>();
		if(count > 0) {
			for(Bill bill : billsList) {
				//companies.add(new Company(bill.getCore(), bill.getId()));
			}
		}
		Map<String, Object> result = new HashMap<>();
		result.put("count", count);
		result.put("content", companies);
		msg.set("查询成功", CodeConstant.SUCCESS, result);
		
		return msg;
	}
	
	//获取指定票据
	@PostMapping("/id")
	public Msg id(@RequestParam int id, @SessionAttribute(required=false) Admin admin) {
		Bill bill = billRepository.findOne(id);
		if(bill != null && (bill.getStatus() == 0 || admin != null)) {
			
			//获取收票渠道
			//Map<String , Object> source = AppTool.pageToMap(sourceService.find(0, 10, null, bill.getInvoice()));
			Map<String, Object> result = new HashMap<>();
			result.put("bill", bill);
			//result.put("sources", source);
			
			msg.set("查询成功", CodeConstant.SUCCESS, result);
			
		} else {
			msg.set("未找到此条信息或此信息已关闭", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}
	
	//快速搜索
	@PostMapping("/fast")
	public Msg fast(@SessionAttribute User user, @RequestParam String core,
			@RequestParam int index, @RequestParam int size) {
		//添加搜索记录
		Page<Record> page = recordService.find(0, 1, user, core);
		Record record = null;
		if(page.getTotalElements() > 0) {
			record = page.getContent().get(0);
			record.setTime(new Date(System.currentTimeMillis()));
		} else {
			//第一次搜索
			record = new Record(user, core, new Date(System.currentTimeMillis()));
		}
		record = recordRepository.save(record);
		
		Page<Bill> bills = billService.find(index, size, 0, core, null, null, null);
		msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(bills));
		
		return msg;
	}
	
	//修改票据
	@PostMapping("/set")
	public Msg set(@SessionAttribute Admin admin, @RequestParam int id, @RequestParam(required=false) String invoice, 
			@RequestParam(required=false) BigDecimal rate, @RequestParam(required=false) BigDecimal initiate, 
			@RequestParam(required=false) BigDecimal max, @RequestParam(required=false) BigDecimal total, 
			@RequestParam(required=false) BigDecimal usable,@RequestParam(required=false) Integer shortest, 
			@RequestParam(required=false) String remark, 
			@RequestParam(required=false) Integer type, @RequestParam(required=false) Integer level,
			@RequestParam(required=false) Integer status, @RequestParam(required=false) BigDecimal  min) {
		Bill bill = billRepository.findOne(id);
		if(bill != null) {
			boolean isChange = false;
			//if(CheckTool.isString(invoice) && !bill.getInvoice().equals(invoice)) {
			//	isChange = true;
			//	bill.setInvoice(invoice);
			//}
			if(rate != null && rate.compareTo(bill.getRate()) != 0) {
				isChange = true;
				bill.setRate(rate);
			}
			if(CheckTool.isString(remark) && (bill.getRemark() == null || !bill.getRemark().equals(remark))) {
				isChange = true;
				bill.setRemark(remark);
			}
			if(initiate != null && initiate.compareTo(bill.getInitiate()) != 0) {
				isChange = true;
				bill.setInitiate(initiate);
			}
			if(total != null && total.compareTo(bill.getTotal()) != 0) {
				isChange = true;
				bill.setTotal(total);
			}
			if(max != null && max.compareTo(bill.getMax()) != 0) {
				isChange = true;
				bill.setMax(max);
			}
			if(usable != null && usable.compareTo(bill.getUsable()) != 0) {
				isChange = true;
				bill.setUsable(usable);
			}
			if(shortest != null && shortest.intValue() != bill.getShortest()) {
				isChange = true;
				bill.setShortest(shortest);
			}
			
			if(level != null && bill.getLevel() != level.intValue()) {
				isChange = true;
				bill.setLevel(level);
			}
			if(status != null && bill.getStatus() != status.intValue()) {
				isChange = true;
				bill.setStatus(status);
			}
			if(type != null && bill.getType() != type.intValue()) {
				isChange = true;
				bill.setType(type);
			}
			if(min != null && bill.getMin().compareTo(min) != 0) {
				isChange = true;
				bill.setMin(min);
			}
			if(isChange) {
				bill = billRepository.save(bill);
				if(bill != null) {
					msg.set("修改成功", CodeConstant.SUCCESS, bill);
				} else {
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
	//获取票据列表
	@PostMapping("/find")
	public Msg find(@SessionAttribute Admin admin, @RequestParam int index, @RequestParam int size, 
			@RequestParam(required=false) Integer status, @RequestParam(required=false) String core,
			@RequestParam(required=false) BigDecimal money, @RequestParam(required=false) String invoice, 
			@RequestParam(required=false) Integer type) {
		Page<Bill> page = billService.find(index, size, status, core, money, type, invoice);
		msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(page));
		return msg;
	}
	
	//管理员添加票据
	@PostMapping("/add")
	public Msg add(@SessionAttribute Admin admin, @RequestParam String invoice, @RequestParam String core, 
			@RequestParam BigDecimal rate,@RequestParam BigDecimal initiate, @RequestParam BigDecimal max, @RequestParam BigDecimal min,
			@RequestParam BigDecimal total, @RequestParam BigDecimal usable, @RequestParam int shortest,
			@RequestParam int adjuest, @RequestParam(required=false) String remark,  @RequestParam int level,
			@RequestParam int type, @RequestParam(required=false) String sname,
			@RequestParam(required=false) String sduty, @RequestParam(required=false) String sphone) {
		
		//判断是否是子/孙公司
		type = 0;
		if(core.indexOf("子公司") > 0) {
			type = 1;
		} else if(core.indexOf("孙公司") > 0) {
			type = 2;
		}
		
		if(CheckTool.isString(sphone) && !CheckTool.isPhone(sphone)) {
			msg.set("手机号格式有误", CodeConstant.ERR_PAR, null);
			return msg;
		}
		
		/*
		Bill bill = new Bill(invoice, core, rate, initiate, max, min, total, usable, shortest, adjuest, remark, level, type);
		bill = billRepository.save(bill);
		if(bill != null) {
			
			//判断是否有这个名称的收票企业，如果没有则创建之
			Source source = sourceRepository.findByInvoice(invoice);
			if(source == null) {
				source = new Source(invoice, null, null, null, null, null, null, null, null, null, rate, adjuest);
				if(CheckTool.isString(sname) && CheckTool.isString(sduty) && CheckTool.isPhone(sphone)) {
					Staff staff = new Staff(sname, sphone);
					staff = staffRepository.save(staff);
					source.getStaffs().add(staff);
				}
				source = sourceRepository.save(source);
				
				if(source != null) {
					msg.set("添加成功", CodeConstant.SUCCESS, bill);
				} else {
					msg.set("创建渠道的时候出错", CodeConstant.ERROR, null);
				}
			} else {
				//已经存在渠道，不需要自动创建渠道了
				msg.set("添加成功", CodeConstant.SUCCESS, bill);
				if(CheckTool.isString(sname) && CheckTool.isString(sduty) && CheckTool.isPhone(sphone)) {
					Staff staff = new Staff(sname, sphone);
					staff = staffRepository.save(staff);
					source.getStaffs().add(staff);
					source = sourceRepository.save(source);
				}
			}
			
			
		} else {
			msg.set("添加失败", CodeConstant.SET_ERR, null);
		}
		*/
		return msg;
	}
	 
	@RequestMapping("/test")
	public Msg test() {
		msg.set("成功", CodeConstant.SUCCESS, this.getClass().getName());
		return msg;
	}
}
