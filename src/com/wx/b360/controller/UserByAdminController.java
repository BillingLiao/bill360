package com.wx.b360.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.wx.b360.entity.Admin;
import com.wx.b360.entity.Inventory;
import com.wx.b360.entity.UserByAdmin;
import com.wx.b360.model.Msg;
import com.wx.b360.tool.AppTool;
import com.wx.b360.tool.CheckTool;
import com.wx.b360.tool.CodeConstant;

/**
 * 管理员建用户名Controller
 * 
 * @author Billing
 *
 */
@RestController
@RequestMapping("/userByAdmin")
public class UserByAdminController extends BaseController {

	// 删除用户信息
	@PostMapping("/del")
	public Msg del(@RequestParam int id) {
		UserByAdmin userByAdmin = userByAdminRepository.findOne(id);
		List<Inventory> inventoryList = inventoryRepository.findByUserByAdmin(userByAdmin);
		for (Inventory inventory : inventoryList) {
			if (inventory != null) {
				msg.set("用户有票据库存，请先删除对应票据！", CodeConstant.SET_ERR, null);
				return msg;
			}
		}
		if (userByAdmin != null) {
			userByAdminRepository.delete(userByAdmin);
			msg.set("删除成功", CodeConstant.SUCCESS, null);
		} else {
			msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}

	// 修改用户信息
	@PostMapping("/set")
	public Msg set(@SessionAttribute Admin admin, @RequestParam int id, @RequestParam(required = false) String name,
			@RequestParam(required = false) String company, @RequestParam(required = false) String phone,
			@RequestParam(required = false) String wechat, @RequestParam(required = false) String addr) {

		UserByAdmin userByAdmin = userByAdminRepository.findOne(id);
		if (userByAdmin != null) {
			boolean isChange = false;
			if (CheckTool.isString(name) || CheckTool.isString(company) || CheckTool.isString(phone)) {
				if (!userByAdmin.getCompany().equals(company)) { // 判断与修改前数据是否不一致
					isChange = true;
					userByAdmin.setCompany(company);
				}

				if (!userByAdmin.getName().equals(name)) {
					isChange = true;
					userByAdmin.setName(name);
				}
				if (!userByAdmin.getPhone().equals(phone)) {
					isChange = true;
					userByAdmin.setPhone(phone);
				}
				if (CheckTool.isString(wechat) && (userByAdmin.getWechat() == null || !userByAdmin.getWechat().equals(wechat))) {
					isChange = true;
					userByAdmin.setWechat(wechat);
				}
				if(userByAdmin.getAddr() != null) {
					if (!userByAdmin.getAddr().equals(addr)) {
						isChange = true;
						userByAdmin.setAddr(addr);
					}
				}else{
					 if(CheckTool.isString(wechat)) {
						 isChange = true;
						userByAdmin.setAddr(addr);
					 }
					
				}
				if (isChange) {
					userByAdmin = userByAdminRepository.save(userByAdmin);
					msg.set("修改成功", CodeConstant.SUCCESS, userByAdmin);
				} else {
					msg.set("没有要修改的数据", CodeConstant.ERROR, null);
				}
			} else {
				msg.set("数据不能为空", CodeConstant.ERROR, null);
			}

		} else {
			msg.set("未找到此条信息", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}

	// 获取指定用户信息
	@PostMapping("/id")
	public Msg findById(@SessionAttribute Admin admin, @RequestParam int id) {
		UserByAdmin userByAdmin = userByAdminRepository.findOne(id);
		if (userByAdmin != null) {
			msg.set("查询成功", CodeConstant.SUCCESS, userByAdmin);
		} else {
			msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}

	// 获取持票信息列表
	@PostMapping("/find")
	public Msg find(@RequestParam int index, @RequestParam int size, @RequestParam(required = false) String name,
			@RequestParam(required = false) String company) {
		Page<UserByAdmin> page = userByAdminService.find(index, size, name, company);
		msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(page));
		return msg;
	}

	/**
	 * 管理员查询所有用户姓名
	 * 
	 * @return
	 */
	@PostMapping("/findUserName")
	public List<UserByAdmin> findUserName() {
		List<UserByAdmin> userNamelist = userByAdminRepository.findUserName();
		return userNamelist;
	}

	// 添加用户信息
	@PostMapping("/add")
	public Msg add(@RequestParam String name, @RequestParam String company, @RequestParam String phone,
			@RequestParam(required = false) String wechat, @RequestParam(required = false) String addr) {

		UserByAdmin userByAdmin = new UserByAdmin(addr, company, name, phone, wechat);
		//System.out.println(userByAdmin.getWechat());
		userByAdminRepository.save(userByAdmin);
		msg.set("添加成功", CodeConstant.SUCCESS, userByAdmin);
		return msg;
	}

	@RequestMapping("/test")
	public Msg test() {
		msg.set("成功", CodeConstant.SUCCESS, this.getClass().getName());
		return msg;
	}

}
