package com.wx.b360.controller;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.wx.b360.entity.Admin;
import com.wx.b360.entity.Bill;
import com.wx.b360.entity.Inventory;
import com.wx.b360.entity.Order;
import com.wx.b360.entity.User;
import com.wx.b360.model.Msg;
import com.wx.b360.tool.AppTool;
import com.wx.b360.tool.CheckTool;
import com.wx.b360.tool.CodeConstant;

@RestController
@RequestMapping("/order")
public class OrderController extends BaseController {

	// 管理员处理订单
	@PostMapping("/set")
	public Msg set(@SessionAttribute Admin admin, @RequestParam int id, @RequestParam int status) {
		Order order = orderRepository.findOne(id);
		if (order != null) {
			boolean isChange = false;
			if (order.getStatus() != status) {
				isChange = true;
				order.setStatus(status);
			}
			if (isChange) {
				order.setStime(new Date(System.currentTimeMillis()));
				order = orderRepository.save(order);
				msg.set("更新成功", CodeConstant.SUCCESS, order);
			} else {
				msg.set("没有要修改的数据", CodeConstant.ERROR, null);
			}

		} else {
			msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}

	// 获取订单列表
	@PostMapping("/find")
	public Msg find(@SessionAttribute Admin admin, @RequestParam int index, @RequestParam int size,
			@RequestParam(required = false) Integer status, @RequestParam(required = false) Integer userId,
			@RequestParam(required = false) String invoice, @RequestParam(required = false) Integer id) {
		User user = null;
		if (userId != null)
			user = userRepository.findOne(userId);
		Page<Order> page = orderService.find(index, size, status, user, invoice, id);
		msg.set("查找成功", CodeConstant.SUCCESS, AppTool.pageToMap(page));
		return msg;
	}

	// 获取指定订单详情
	@PostMapping("/id")
	public Msg id(@SessionAttribute(required = false) User user, @SessionAttribute(required = false) Admin admin,
			@RequestParam int id) {
		if (user != null) {
			Order order = orderRepository.findOne(id);
			if (order != null && order.getUser().getId() == user.getId()) {
				msg.set("查询成功", CodeConstant.SUCCESS, order);
			} else {
				msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
			}
		} else if (admin != null) {
			Order order = orderRepository.findOne(id);
			if (order != null) {
				msg.set("查询成功", CodeConstant.SUCCESS, order);
			} else {
				msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
			}
		} else {
			msg.set("请先登录", CodeConstant.NO_AUTH, null);
		}
		return msg;
	}

	// 获取指定订单详情并生成文件
	@PostMapping("/id/file")
	public Msg idWithFile(@SessionAttribute(required = false) User user,
			@SessionAttribute(required = false) Admin admin, @RequestParam int id) {
		if (user != null) {
			Order order = orderRepository.findOne(id);
			if (order != null && order.getUser().getId() == user.getId()) {
				msg.set("查询成功", CodeConstant.SUCCESS, order);
			} else {
				msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
			}
		} else if (admin != null) {
			Order order = orderRepository.findOne(id);
			if (order != null) {
				msg.set("查询成功", CodeConstant.SUCCESS, order);
			} else {
				msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
			}
		} else {
			msg.set("请先登录", CodeConstant.NO_AUTH, null);
		}
		return msg;
	}

	// 计算计息天数、每十万贴息和利息
	@PostMapping("/calc")
	public Msg calc(@RequestParam BigDecimal money, @RequestParam String time, @RequestParam int billId) {
		if (CheckTool.isDate(time)) {
			Bill bill = billRepository.findOne(billId);
			if (bill != null) {
				Date dateEnt = AppTool.changeDate(time);
				if (dateEnt.getTime() < System.currentTimeMillis()) {
					msg.set("到期日期不能小于当前时间", CodeConstant.ERR_PAR, null);
					return msg;
				}

				int day = (int) ((dateEnt.getTime() - System.currentTimeMillis()) / 86400000 + 1);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dateEnt);
				if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
					// 周日情况
					day += 1;
				} else if (calendar.get(Calendar.DAY_OF_WEEK) == 7) {
					// 周六情况
					day += 2;
				}
				
				int offer = bill.getOffer();
				BigDecimal interest = null;
				String interestInfo = "";
				BigDecimal interestUnit = null;
				String interestUnitUnitInfo = "";
				//年化: 利率+调整天数
				if(offer == 0) {
					
					day += bill.getAdjuest();
					
					BigDecimal rate = bill.getRate();
					interest = money.multiply(rate).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP)
							.divide(new BigDecimal(360), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(day));
					interestInfo = money + "×" + rate + "÷100÷360×" + day + "=" + interest;
					interestUnit = interest.multiply(new BigDecimal(100000)).divide(money, 4,
							BigDecimal.ROUND_HALF_UP);
					interestUnitUnitInfo = interest + "×" + "100000" + "÷" + money + "=" + interestUnit;	
				}
				//每十万扣费：deductions 
				else if(offer == 1){
					BigDecimal deductions = bill.getDeductions();
					interestUnit = deductions;
					interestUnitUnitInfo = deductions + "";
					//interestUnit = deductions.divide(new BigDecimal(360), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(day));
					//interestUnitUnitInfo = interestUnit + "÷360×" + day + "=" + interestUnit;
					interest = interestUnit.multiply(money).divide(new BigDecimal(100000), 4, BigDecimal.ROUND_HALF_UP);
					interestInfo = interestUnit + "÷100000×" + money + "=" + interest;
					
					//先除再乘，小数会丢
					/*interest = interestUnit.divide(new BigDecimal(100000), 4, BigDecimal.ROUND_HALF_UP)
							.multiply(money);*/
				}
				//直接扣百分比：direct
				else if(offer == 2) {
					BigDecimal direct = bill.getDirect();
					interest = money.multiply(direct).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP);
					interestInfo = money + "×" + direct + "÷100 = " + interest ;
					interestUnit = interest.multiply(new BigDecimal(100000)).divide(money, 4,
							BigDecimal.ROUND_HALF_UP);
					interestUnitUnitInfo = interest + "×" + "100000" + "÷" + money + "=" + interestUnit;
				}
				//年化+每十万扣费
				else if(offer == 3) {
					day += bill.getAdjuest();
					BigDecimal rate = bill.getRate();
					BigDecimal deductions = bill.getDeductions();
					
					BigDecimal interest1 = money.multiply(rate).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP)
							.divide(new BigDecimal(360), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(day));
					BigDecimal interestUnit1 = interest1.multiply(new BigDecimal(100000)).divide(money, 4,
							BigDecimal.ROUND_HALF_UP);
					BigDecimal interestUnit2 = deductions;
					BigDecimal interest2 = interestUnit2.multiply(money).divide(new BigDecimal(100000), 4, BigDecimal.ROUND_HALF_UP);
					
					interest = interest1.add(interest2);
					interestUnit = interestUnit1.add(interestUnit2);
					interestInfo = "("+interestUnit + "÷100000×" + money+") + (" + money + "×" + rate + "÷100÷360×" + day + ")" + "=" + interest;
					interestUnitUnitInfo = deductions + "+" + interest + "×" + "100000" + "÷" + money + "=" + interestUnit;
					
				}
				
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("day", day);
				result.put("interest", interest);
				result.put("interestInfo", interestInfo);
				result.put("interestUnit", interestUnit);
				result.put("interestUnitUnitInfo", interestUnitUnitInfo);
				msg.set("查询成功", CodeConstant.SUCCESS, result);

			} else {
				msg.set("未找到此条收票渠道", CodeConstant.FIND_ERR, null);
			}
		} else {
			msg.set("日期格式有误", CodeConstant.ERR_PAR, null);
		}
		return msg;
	}

	// 获取当前用户的订单
	@PostMapping("/my")
	public Msg my(@SessionAttribute User user, @RequestParam int index, @RequestParam int size,
			@RequestParam(required = false) Integer status) {
		Page<Order> page = orderService.find(index, size, status, user, null, null);
		msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(page));
		return msg;
	}

	// 生成订单
	@PostMapping("/add")
	public Msg add(@SessionAttribute User user, @RequestParam int billId,
			@RequestParam BigDecimal money, @RequestParam String time, 
			@RequestParam String img, @RequestParam BigDecimal subsidy) {
		Bill bill = billRepository.findOne(billId);
		if (bill != null && bill.getStatus() == 0) {
			if (CheckTool.isDate(time)) {
				Date date = AppTool.changeDate(time);
	
					if (date.getTime() < System.currentTimeMillis()) {
						msg.set("到期日期小于当前时间", CodeConstant.ERR_PAR, null);
						return msg;
					}

					int day = (int) ((date.getTime() - System.currentTimeMillis()) / 86400000 + 1);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
						// 周日情况
						day += 1;
					} else if (calendar.get(Calendar.DAY_OF_WEEK) == 7) {
						// 周六情况
						day += 2;
					}
					
					int offer = bill.getOffer();
					BigDecimal interest = null;
					BigDecimal interestUnit = null;
					//年化: 利率+调整天数
					if(offer == 0) {
						
						day += bill.getAdjuest();
						
						BigDecimal rate = bill.getRate();
						interest = money.multiply(rate).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP)
								.divide(new BigDecimal(360), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(day));
						interestUnit = new BigDecimal(100000).multiply(rate)
								.divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP)
								.divide(new BigDecimal(360), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(day));
					}
					//每十万扣费：deductions 
					else if(offer == 1){
						BigDecimal deductions = bill.getDeductions();
						interestUnit = deductions;
						interest = interestUnit.multiply(money).divide(new BigDecimal(100000), 4, BigDecimal.ROUND_HALF_UP);
						/*interest = interestUnit.divide(new BigDecimal(100000), 4, BigDecimal.ROUND_HALF_UP)
								.multiply(money);*/
					}
					//直接扣百分比：direct
					else if(offer == 2) {
						BigDecimal direct = bill.getDirect();
						interest = money.multiply(direct).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP);
						interestUnit = interest.multiply(new BigDecimal(100000)).divide(money, 4,
								BigDecimal.ROUND_HALF_UP);
					}
					//年化+每十万扣费
					else if(offer == 3) {
						day += bill.getAdjuest();
						BigDecimal rate = bill.getRate();
						BigDecimal deductions = bill.getDeductions();
						
						BigDecimal interest1 = money.multiply(rate).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP)
								.divide(new BigDecimal(360), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(day));
						BigDecimal interestUnit1 = interest1.multiply(new BigDecimal(100000)).divide(money, 4,
								BigDecimal.ROUND_HALF_UP);
						BigDecimal interestUnit2 = deductions;
						BigDecimal interest2 = interestUnit2.multiply(money).divide(new BigDecimal(100000), 4, BigDecimal.ROUND_HALF_UP);
						
						interest = interest1.add(interest2);
						interestUnit = interestUnit1.add(interestUnit2);
					}
					
					Order order = new Order(bill, money, img, date, subsidy, interest, day, user);
					order = orderRepository.save(order);
					msg.set("添加成功", CodeConstant.SUCCESS, order);

			} else {
				msg.set("时间格式有误", CodeConstant.ERR_PAR, null);
			}

		} else {
			msg.set("未找到此条信息", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}

	@RequestMapping("/test")
	public Msg test() {
		msg.set("成功", CodeConstant.SUCCESS, this.getClass().getName());
		return msg;
	}
}
