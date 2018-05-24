package com.wx.b360.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
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

import com.wx.b360.entity.Acceptance;
import com.wx.b360.entity.Admin;
import com.wx.b360.entity.Bill;
import com.wx.b360.entity.Order;
import com.wx.b360.entity.Staff;
import com.wx.b360.entity.User;
import com.wx.b360.model.Company;
import com.wx.b360.model.Msg;
import com.wx.b360.tool.AppTool;
import com.wx.b360.tool.CheckTool;
import com.wx.b360.tool.CodeConstant;

@RestController
@RequestMapping("/bill")
public class BillController extends BaseController {

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
	public Msg precise(@RequestParam String core,@RequestParam Integer type, @RequestParam String invoice, @SessionAttribute User user) {
		if (user.getCard() == 1) {
			Acceptance acceptance = acceptanceRepository.findByCoreAndInvoiceAndType(core, invoice, type);
			List<Bill> billList = billRepository.findByAcceptance(acceptance);
			msg.set("查询成功", CodeConstant.SUCCESS, billList);
		} else if (user.getPrecise() < 5) {
			user.setPrecise(user.getPrecise() + 1);
			user = userRepository.save(user);
			// 更新缓存
			ValueOperations<String, User> operations = redisTemplate.opsForValue();
			operations.set("user:" + user.getId(), user);
			Acceptance acceptance = acceptanceRepository.findByCoreAndInvoiceAndType(core, invoice, type);
			List<Bill> billList = billRepository.findByAcceptance(acceptance);
			msg.set("查询成功", CodeConstant.SUCCESS, billList);
		} else {
			msg.set("请先上传名片", CodeConstant.ERROR, null);
		}
		return msg;
	}
	
	/*
	// 快速搜索
	@PostMapping("/fast")
	public Msg fast(@SessionAttribute User user, @RequestParam String core, @RequestParam int index,
			@RequestParam int size) {
		// 添加搜索记录
		Page<Record> page = recordService.find(0, 1, user, core);
		Record record = null;
		if (page.getTotalElements() > 0) {
			record = page.getContent().get(0);
			record.setTime(new Date(System.currentTimeMillis()));
		} else {
			// 第一次搜索
			record = new Record(user, core, new Date(System.currentTimeMillis()));
		}
		record = recordRepository.save(record);
		
		Page<Bill> bills = billService.find(index, size, null, null, core, null);
		msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(bills));
		return msg;
	}*/

	// 企业核心名模糊搜索
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
	}
	
	/**
	 * 通过核心企业名 查找 利率最小的 bill 收票清单
	 * @param user
	 * @param core
	 * @return
	 */
	@PostMapping("/findByCore")
	public Msg findMinRateByCore(@SessionAttribute User user,@RequestParam String core) {
		
		Bill bill = billRepository.findMinRateBycore(core);
		
		if(bill != null && bill.getStatus() == 0) {
			msg.set("查询成功", CodeConstant.SUCCESS, bill);
		}else {
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
	public Msg set(@SessionAttribute Admin admin, @RequestParam int id, @RequestParam int staffId, @RequestParam String core, @RequestParam(required = false) String invoice,
			@RequestParam(required = false) BigDecimal rate, @RequestParam(required = false) BigDecimal total,
			@RequestParam BigDecimal deductions, @RequestParam String max, @RequestParam BigDecimal min,
			@RequestParam  Integer shortest, @RequestParam Integer longest, @RequestParam(required = false) Integer adjuest, @RequestParam(required = false) Integer etime,
			@RequestParam(required = false) BigDecimal usable, @RequestParam int status,
			@RequestParam(required = false) int level, @RequestParam int is_bargain, @RequestParam int is_invoice,@RequestParam int agreement,
			@RequestParam int is_financing, @RequestParam int is_clean, @RequestParam int is_moneyorback,
			@RequestParam(required = false) String offer, @RequestParam(required = false) String remark) {
		
		Acceptance acceptance = acceptanceRepository.findByCoreAndInvoice(core, invoice);
		if(acceptance != null) {
			int acceptanceId = acceptance.getId();
			Bill bill = billRepository.findByStaffAndAcceptance(staffId,acceptanceId);
			if(bill != null && id != bill.getId()) {
				msg.set("已有该条数据", CodeConstant.SET_ERR, null);
				return msg;
			}
		}else {
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
			if (CheckTool.isString(invoice) && (acceptance.getInvoice() == null || !acceptance.getInvoice().equals(invoice))) {
				isChange = true;
				acceptance.setInvoice(invoice);
			}
			if (rate.compareTo(bill.getRate()) != 0) {
				isChange = true;
				bill.setRate(rate);
			}
			if (total != null && total.compareTo(bill.getTotal()) != 0) {
				isChange = true;
				bill.setTotal(total);
			}
			if (deductions.compareTo(bill.getDeductions()) != 0) {
				isChange = true;
				bill.setDeductions(deductions);
			}
			if (CheckTool.isString(max) && (bill.getMax() == null || !bill.getMax().equals(max))) {
				isChange = true;
				acceptance.setInvoice(invoice);
			}
			if (bill.getMin().compareTo(min) != 0) {
				isChange = true;
				bill.setMin(min);
			}
			if (shortest != null && shortest.compareTo(bill.getShortest()) != 0) {
				isChange = true;
				bill.setShortest(shortest);
			}
			if (longest != null && longest.compareTo(bill.getLongest()) != 0) {
				isChange = true;
				bill.setLongest(longest);
			}
			if (adjuest != null && adjuest.compareTo(bill.getAdjuest()) != 0) {
				isChange = true;
				bill.setAdjuest(adjuest);
			}
			if (etime != null && etime.compareTo(bill.getEtime()) != 0) {
				isChange = true;
				bill.setEtime(etime);
			}
			if (usable != null && usable.compareTo(bill.getUsable()) != 0) {
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
			if (CheckTool.isString(offer) && (bill.getOffer() == null || !bill.getOffer().equals(offer))) {
				isChange = true;
				bill.setOffer(offer);
			}

			if (CheckTool.isString(remark) && (bill.getRemark() == null || !bill.getRemark().equals(remark))) {
				isChange = true;
				bill.setRemark(remark);
			}
			
			if (isChange) {
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
	public Msg find(@SessionAttribute Admin admin, @RequestParam int index, @RequestParam int size, @RequestParam(required = false) String staffName,
			@RequestParam(required = false) String core,@RequestParam(required = false) String invoice,
			@RequestParam(required = false) Integer status) {
		Page<Bill> page = billService.find(index, size, staffName, status, null, core, invoice);
		msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(page));
		return msg;
	}

	// 管理员添加收票清单
	@PostMapping("/add")
	public Msg add(@SessionAttribute Admin admin, @RequestParam int staffId, @RequestParam String core,@RequestParam(required = false) String invoice,
			@RequestParam(required = false) BigDecimal rate, @RequestParam(required = false) BigDecimal total,
			@RequestParam(required = false) BigDecimal deductions, @RequestParam String max, @RequestParam BigDecimal min,
			@RequestParam Integer shortest, @RequestParam Integer longest, @RequestParam Integer adjuest, @RequestParam(required = false) int etime,
			@RequestParam(required = false) BigDecimal usable, @RequestParam(required = false) int status,
			@RequestParam(required = false) int level, @RequestParam int is_bargain, @RequestParam int is_invoice, @RequestParam int agreement,
			@RequestParam int is_financing, @RequestParam int is_clean, @RequestParam int is_moneyorback,
			@RequestParam(required = false) String offer, @RequestParam(required = false) String remark) {

		/*
		 * if(CheckTool.isString(sphone) && !CheckTool.isPhone(sphone)) {
		 * msg.set("手机号格式有误", CodeConstant.ERR_PAR, null); return msg; }
		 */
		Acceptance acceptance = acceptanceRepository.findByCoreAndInvoice(core, invoice);
		if(acceptance == null) {
			acceptance = new Acceptance(invoice, core, null, null, 0, null, null, 0);
		}
		int acceptanceId = acceptance.getId();
		
		Bill bill = billRepository.findByStaffAndAcceptance(staffId,acceptanceId);
		
		if(bill == null) {
			
		acceptance = acceptanceRepository.save(acceptance);
		
		Staff staff = staffRepository.findOne(staffId);

		bill = new Bill(acceptance, staff, rate, shortest, longest, adjuest, deductions, max, min, total, usable, remark, status, level,is_bargain, is_invoice, agreement, is_moneyorback, is_financing, is_clean, etime, offer);
		bill = billRepository.save(bill);
		if (bill != null) {
			msg.set("添加成功", CodeConstant.SUCCESS, bill);

		} else {
			msg.set("添加失败", CodeConstant.SET_ERR, null);
		}
		}else {
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
