package com.wx.b360.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wx.b360.entity.Acceptance;
import com.wx.b360.entity.Admin;
import com.wx.b360.entity.Bill;
import com.wx.b360.entity.Order;
import com.wx.b360.entity.Staff;
import com.wx.b360.entity.User;
import com.wx.b360.model.Company;
import com.wx.b360.model.Msg;
import com.wx.b360.model.Bill_import;
import com.wx.b360.tool.AppTool;
import com.wx.b360.tool.CheckTool;
import com.wx.b360.tool.CodeConstant;
import com.wx.b360.tool.ImportExcelTool;

@RestController
@RequestMapping("/bill")
public class BillController extends BaseController {

	/**
	 * 上传excel 收票清单导入数据库
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@Transactional
	@PostMapping("/importExcel")
	public Msg importExcel(@SessionAttribute Admin admin, @RequestParam MultipartFile file) throws IOException {
		byte[] fBytes = file.getBytes();
		InputStream fis = new ByteArrayInputStream(fBytes);
		Map<String, String> m = new HashMap<String, String>();
		m.put("公司", "staffCompany");
		m.put("姓名", "staffName");
		m.put("电话", "staffPhone");
		m.put("收票主体", "acceptanceCore");
		m.put("承兑企业", "acceptanceInvoice");
		m.put("总额度（万）", "total");
		m.put("可用额度（万）", "usable");
		m.put("收票单张起步金额（万）", "min");
		m.put("收票单张上限金额（万）", "max");
		m.put("收票起步天数（DAY）", "shortest");
		m.put("收票最高天数（DAY）", "longest");
		m.put("背书次数限制", "etime");
		m.put("合同", "isBargain");
		m.put("发票", "isInvoice");
		m.put("质押协议", "agreement");
		m.put("融资票", "isFinancing");
		m.put("光票", "isClean");
		m.put("背款", "isMoneyOrBack");
		m.put("报价方式", "offer");
		m.put("利率", "rate");
		m.put("调整天数", "adjuest");
		m.put("每10万扣", "deductions");
		m.put("优先级", "level");
		m.put("直接扣", "direct");
		m.put("状态", "status");
		m.put("备注", "remark");
		List<Map<String, Object>> ls;
		try {
			ls = ImportExcelTool.parseExcel(fis, file.getOriginalFilename(), m);
			String string = JSON.toJSONString(ls);
			if (string == null || string.equals("[]")) {
				msg.set("表格内容不能为空", CodeConstant.SET_ERR, null);
			} else {
				Bill_import billImport = null;
				JSONArray platformList = JSON.parseArray(string);
				for (Object jsonObject : platformList) {
					billImport = JSONObject.parseObject(jsonObject.toString(), Bill_import.class);
					String core = billImport.getAcceptanceCore();
					String invoice = billImport.getAcceptanceInvoice();
					Acceptance acceptance = acceptanceRepository.findByCoreAndInvoice(core, invoice);
					if (acceptance == null) {
						acceptance = new Acceptance(invoice, core, null, null, 0, null, null, 0);
						acceptance = acceptanceRepository.save(acceptance);
					}
					String name = billImport.getStaffName();
					String company = billImport.getStaffCompany();
					String phone = billImport.getStaffPhone();
					Staff staff = staffRepository.findByPhone(phone);
					if (staff == null) {
						staff = new Staff(name, company, null, null, phone, null, null, null);
						staff = staffRepository.save(staff);
					}
					BigDecimal rate = billImport.getRate();
					Integer adjuest = billImport.getAdjuest();
					String shortest = billImport.getShortest();
					String longest = billImport.getLongest();
					String max = billImport.getMax();
					String min = billImport.getMin();
					String total = billImport.getTotal();
					String usable = billImport.getUsable();
					Integer status = billImport.getStatus();
					Integer level = billImport.getLevel();
					String is_bargain = billImport.getIsBargain();
					String is_invoice = billImport.getIsInvoice();
					String s_agreement = billImport.getAgreement();
					String is_moneyOrBack = billImport.getIsMoneyOrBack();
					String is_financing = billImport.getIsFinancing();
					String is_clean = billImport.getIsClean();
					
					Integer isBargain = CheckTool.stringToInt(is_bargain);
					Integer isInvoice = CheckTool.stringToInt(is_invoice);
					Integer agreement = CheckTool.stringToInt(s_agreement);
					Integer isMoneyOrBack = CheckTool.stringToInt(is_moneyOrBack);
					Integer isFinancing = CheckTool.stringToInt(is_financing);
					Integer isClean = CheckTool.stringToInt(is_clean);
					
					String etime = billImport.getEtime();
					Integer offer = billImport.getOffer();
					BigDecimal deductions = billImport.getDeductions();
					BigDecimal direct = billImport.getDirect();
					String remark = billImport.getRemark();
					Bill bill = new Bill(acceptance, staff, rate, shortest, longest, adjuest, deductions, direct, max, min, total,
							usable, remark, status, level, isBargain, isInvoice, agreement, isMoneyOrBack, isFinancing,
							isClean, etime, offer);
					BigDecimal aYInterest = billService.calculationInterest(bill);
					bill.setAYInterest(aYInterest);
					Bill bill2 = billRepository.findByStaffAndAcceptance(staff.getId(), acceptance.getId());
					if(bill2 != null) {
						bill.setId(bill2.getId());
					}
					acceptance = acceptanceRepository.save(acceptance);
					bill = billRepository.save(bill);
				}
				msg.set("导入成功", CodeConstant.SUCCESS, null);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.set("导入失败，请检查数据是否正确!", CodeConstant.SET_ERR, null);
			
		}
		return msg;
	}

	// 获取收票清单和与之关联的对接人信息、承兑企业信息
	@PostMapping("/id/source")
	public Msg findByIdWithSource(@SessionAttribute Admin admin, @RequestParam int id) {
		Bill bill = billRepository.findOne(id);
		if (bill != null) {
			/*
			 * Map<String, Object> result = new HashMap<>(); result.put("bill", bill);
			 */

			msg.set("查询成功", CodeConstant.SUCCESS, bill);
		} else {
			msg.set("未找到此条信息", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}

	// 删除票据
	@PostMapping("/del")
	public Msg del(@SessionAttribute Admin admin, @RequestParam int id) {
		Bill bill = billRepository.findOne(id);
		List<Order> orderList = orderRepository.findByBill(bill);
		for (Order order : orderList) {
			if (order != null) {
				msg.set("该项有订单生成，请先删除对应订单！", CodeConstant.SET_ERR, null);
				return msg;
			}
		}
		if (bill != null) {
			billRepository.delete(bill);
			msg.set("删除成功", CodeConstant.SUCCESS, null);
		} else {
			msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}

	// 精确搜索
	@PostMapping("/precise")
	public Msg precise(@RequestParam String core, @RequestParam String invoice,
			@SessionAttribute User user) {
		if (user.getCard() == 1) {
			//同时查询 承兑企业为空的bill
			List<Bill> billList = billRepository.findByCoreInvoiceType(core, invoice);
			msg.set("查询成功", CodeConstant.SUCCESS, billList);
		} else if (user.getPrecise() < 5) {
			user.setPrecise(user.getPrecise() + 1);
			user = userRepository.save(user);
			// 更新缓存
			ValueOperations<String, User> operations = redisTemplate.opsForValue();
			operations.set("user:" + user.getId(), user);
			List<Bill> billList = billRepository.findByCoreInvoiceType(core, invoice);
			msg.set("查询成功", CodeConstant.SUCCESS, billList);
		} else {
			msg.set("请先上传名片", CodeConstant.ERROR, null);
		}
		return msg;
	}

	/*
	 * // 快速搜索
	 * 
	 * @PostMapping("/fast") public Msg fast(@SessionAttribute User
	 * user, @RequestParam String core, @RequestParam int index,
	 * 
	 * @RequestParam int size) { // 添加搜索记录 Page<Record> page = recordService.find(0,
	 * 1, user, core); Record record = null; if (page.getTotalElements() > 0) {
	 * record = page.getContent().get(0); record.setTime(new
	 * Date(System.currentTimeMillis())); } else { // 第一次搜索 record = new
	 * Record(user, core, new Date(System.currentTimeMillis())); } record =
	 * recordRepository.save(record);
	 * 
	 * Page<Bill> bills = billService.find(index, size, null, null, core, null);
	 * msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(bills)); return msg;
	 * }
	 */

	/*// 企业核心名模糊搜索
	@PostMapping("/core")
	public Msg core(@RequestParam int index, @RequestParam int size, @RequestParam String core) {
		Page<Bill> bills = billService.find(index, size, null, null, null, core, null);
		List<Bill> billsList = bills.getContent();
		long count = bills.getTotalElements();
		ArrayList<Company> companies = new ArrayList<>();
		if (count > 0) {
			for (Bill bill : billsList) {
				companies.add(new Company(bill.getAcceptance().getCore(), bill.getId()));
			}
		}
		Map<String, Object> result = new HashMap<>();
		result.put("count", count);
		result.put("content", companies);
		msg.set("查询成功", CodeConstant.SUCCESS, result);

		return msg;
	}*/

	/**
	 * 通过核心企业名 查找 一年每十万 利息最低的一条  bill 收票清单
	 * 
	 * @param user
	 * @param core
	 * @return
	 */
	@PostMapping("/fastByCore")
	public Msg fastByCore(@SessionAttribute User user, @RequestParam String core) {

		Bill bill = billRepository.fastByCore(core);

		if (bill != null && bill.getStatus() == 0) {
			msg.set("查询成功", CodeConstant.SUCCESS, bill);
		} else {
			msg.set("查询失败", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}
	
	/**
	 * 通过核心企业名 查找 利率最小的 bill 收票清单
	 * 
	 * @param user
	 * @param core
	 * @return
	 */
	@PostMapping("/findMinByCore")
	public Msg findMinRateByCore(@SessionAttribute User user, @RequestParam String core) {

		Bill bill = billRepository.findMinRateBycore(core);

		if (bill != null && bill.getStatus() == 0) {
			msg.set("查询成功", CodeConstant.SUCCESS, bill);
		} else {
			msg.set("查询失败", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}

	// 获取指定收票清单
	@PostMapping("/id")
	public Msg id(@RequestParam int id, @SessionAttribute(required = false) Admin admin) {
		Bill bill = billRepository.findOne(id);
		if (bill != null && (bill.getStatus() == 0 || admin != null)) {

			msg.set("查询成功", CodeConstant.SUCCESS, bill);

		} else {
			msg.set("未找到此条信息或此信息已关闭", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}

	// 修改对接人收票清单
	@PostMapping("/set")
	public Msg set(@SessionAttribute Admin admin, @RequestParam int id, @RequestParam int staffId,
			@RequestParam String core, @RequestParam(required = false) String invoice,
			@RequestParam(required = false) BigDecimal rate, @RequestParam(required = false) String total,
			@RequestParam(required = false) BigDecimal deductions, @RequestParam(required = false) BigDecimal direct,
			@RequestParam(required = false) String max, @RequestParam(required = false) String min, @RequestParam(required = false) String shortest,
			@RequestParam(required = false) String longest, @RequestParam(required = false) Integer adjuest,
			@RequestParam(required = false) String etime, @RequestParam(required = false) String usable,
			@RequestParam int status, @RequestParam(required = false) int level, @RequestParam int is_bargain,
			@RequestParam int is_invoice, @RequestParam int agreement, @RequestParam int is_financing,
			@RequestParam int is_clean, @RequestParam int is_moneyorback, @RequestParam Integer offer,
			@RequestParam(required = false) String remark) {

		Acceptance acceptance = acceptanceRepository.findByCoreAndInvoice(core, invoice);
		if (acceptance != null) {
			int acceptanceId = acceptance.getId();
			Bill bill = billRepository.findByStaffAndAcceptance(staffId, acceptanceId);
			if (bill != null && id != bill.getId()) {
				msg.set("已有该条数据", CodeConstant.SET_ERR, null);
				return msg;
			}
		} else {
			acceptance = new Acceptance(invoice, core, null, null, 0, null, null, 0);
		}

		Bill bill = billRepository.findOne(id);
		if (bill != null) {
			boolean isChange = false;
			if (staffId != bill.getStaff().getId()) {
				isChange = true;
				Staff staff = staffRepository.findOne(staffId);
				bill.setStaff(staff);
			}
			if (CheckTool.isString(core) && (acceptance.getCore() == null || !acceptance.getCore().equals(core))) {
				isChange = true;
				acceptance.setCore(core);
			}
			if (CheckTool.isString(invoice)
					&& (acceptance.getInvoice() == null || !acceptance.getInvoice().equals(invoice))) {
				isChange = true;
				acceptance.setInvoice(invoice);
			}
			if (rate != null && (bill.getRate() == null || rate.compareTo(bill.getRate()) != 0)) {
				isChange = true;
				bill.setRate(rate);
			}
			if (CheckTool.isString(total) && (bill.getTotal() == null || !bill.getTotal().equals(total))) {
				isChange = true;
				bill.setTotal(total);
			}
			if (deductions != null && (bill.getDeductions() == null || deductions.compareTo(bill.getDeductions()) != 0)) {
				isChange = true;
				bill.setDeductions(deductions);
			}
			if (direct != null && (bill.getDirect() == null || direct.compareTo(bill.getDirect()) != 0)) {
				isChange = true;
				bill.setDirect(direct);
			}
			if (CheckTool.isString(max) && (bill.getMax() == null || !bill.getMax().equals(max))) {
				isChange = true;
				bill.setMax(max);
			}
			if (CheckTool.isString(min) && (bill.getMin() == null || !bill.getMin().equals(min))) {
				isChange = true;
				bill.setMin(min);
			}
			if (CheckTool.isString(shortest) && (bill.getShortest() == null || !bill.getShortest().equals(shortest))) {
				isChange = true;
				bill.setShortest(shortest);
			}
			if (CheckTool.isString(longest) && (bill.getLongest() == null || !bill.getLongest().equals(longest))) {
				isChange = true;
				bill.setLongest(longest);
			}
			if (adjuest != null && adjuest.compareTo(bill.getAdjuest()) != 0) {
				isChange = true;
				bill.setAdjuest(adjuest);
			}
			if (CheckTool.isString(etime) && (bill.getEtime() == null || !bill.getEtime().equals(etime))) {
				isChange = true;
				bill.setEtime(etime);
			}
			if (CheckTool.isString(usable) && (bill.getUsable() == null || !bill.getUsable().equals(usable))) {
				isChange = true;
				bill.setUsable(usable);
			}
			if (status != bill.getStatus()) {
				isChange = true;
				bill.setStatus(status);
			}
			if (bill.getLevel() != level) {
				isChange = true;
				bill.setLevel(level);
			}
			if (is_bargain != bill.getIsBargain()) {
				isChange = true;
				bill.setIsBargain(is_bargain);
			}

			if (is_invoice != bill.getIsInvoice()) {
				isChange = true;
				bill.setIsInvoice(is_invoice);
			}

			if (agreement != bill.getAgreement()) {
				isChange = true;
				bill.setAgreement(agreement);
			}

			if (is_financing != bill.getIsFinancing()) {
				isChange = true;
				bill.setIsFinancing(is_financing);
			}

			if (is_clean != bill.getIsClean()) {
				isChange = true;
				bill.setIsClean(is_clean);
			}
			if (is_moneyorback != bill.getIsMoneyOrBack()) {
				isChange = true;
				bill.setIsMoneyOrBack(is_moneyorback);
			}
			if (bill.getOffer().compareTo(offer) != 0) {
				isChange = true;
				bill.setOffer(offer);
			}

			if (CheckTool.isString(remark) && (bill.getRemark() == null || !bill.getRemark().equals(remark))) {
				isChange = true;
				bill.setRemark(remark);
			}

			if (isChange) {
				BigDecimal aYInterest = billService.calculationInterest(bill);
				bill.setAYInterest(aYInterest);
				acceptance = acceptanceRepository.save(acceptance);
				bill = billRepository.save(bill);
				if (bill != null) {
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

	// 分页查询对接人收票清单
	@PostMapping("/find")
	public Msg find(@SessionAttribute Admin admin, @RequestParam int index, @RequestParam int size,
			@RequestParam(required = false) String staffName, @RequestParam(required = false) String core,
			@RequestParam(required = false) String invoice, @RequestParam(required = false) Integer status) {
		Page<Bill> page = billService.find(index, size, staffName, status, null, core, invoice);
		msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(page));
		return msg;
	}

	// 管理员添加收票清单
	@PostMapping("/add")
	public Msg add(@SessionAttribute Admin admin, @RequestParam int staffId, @RequestParam String core,
			@RequestParam(required = false) String invoice, @RequestParam(required = false) BigDecimal rate,
			@RequestParam(required = false) String total, @RequestParam(required = false) BigDecimal deductions,
			@RequestParam(required = false) BigDecimal direct, @RequestParam(required = false) String max, @RequestParam(required = false) String min,
			@RequestParam(required = false)String shortest, @RequestParam(required = false) String longest,
			@RequestParam(required = false) Integer adjuest, @RequestParam(required = false) String etime,
			@RequestParam(required = false) String usable, @RequestParam(required = false) int status,
			@RequestParam(required = false) int level, @RequestParam Integer is_bargain, @RequestParam Integer is_invoice,
			@RequestParam Integer agreement, @RequestParam Integer is_financing, @RequestParam Integer is_clean,
			@RequestParam Integer is_moneyorback, @RequestParam Integer offer,
			@RequestParam(required = false) String remark) {

		/*
		 * if(CheckTool.isString(sphone) && !CheckTool.isPhone(sphone)) {
		 * msg.set("手机号格式有误", CodeConstant.ERR_PAR, null); return msg; }
		 */
		Acceptance acceptance = acceptanceRepository.findByCoreAndInvoice(core, invoice);
		if (acceptance == null) {
			acceptance = new Acceptance(invoice, core, null, null, 0, null, null, 0);
		}
		int acceptanceId = acceptance.getId();

		Bill bill = billRepository.findByStaffAndAcceptance(staffId, acceptanceId);

		if (bill == null) {

			acceptance = acceptanceRepository.save(acceptance);

			Staff staff = staffRepository.findOne(staffId);

			bill = new Bill(acceptance, staff, rate, shortest, longest, adjuest, deductions, direct, max, min, total,
					usable, remark, status, level, is_bargain, is_invoice, agreement, is_moneyorback, is_financing,
					is_clean, etime, offer);
			BigDecimal aYInterest = billService.calculationInterest(bill);
			bill.setAYInterest(aYInterest);
			bill = billRepository.save(bill);
			if (bill != null) {
				msg.set("添加成功", CodeConstant.SUCCESS, bill);

			} else {
				msg.set("添加失败", CodeConstant.SET_ERR, null);
			}
		} else {
			msg.set("已有该条数据", CodeConstant.SET_ERR, null);
		}
		return msg;
	}

	@RequestMapping("/test")
	public Msg test() {
		msg.set("成功", CodeConstant.SUCCESS, this.getClass().getName());
		return msg;
	}
}
