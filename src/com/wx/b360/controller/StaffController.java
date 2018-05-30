package com.wx.b360.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wx.b360.entity.Admin;
import com.wx.b360.entity.Bill;
import com.wx.b360.entity.Staff;
import com.wx.b360.model.Msg;
import com.wx.b360.tool.AppTool;
import com.wx.b360.tool.CheckTool;
import com.wx.b360.tool.CodeConstant;
import com.wx.b360.tool.ImportExcelTool;

/**
 * 對接人 controller
 * 
 * @author Billing
 *
 */
@RestController
@RequestMapping("/staff")
public class StaffController extends BaseController {

	/**
	 * 上传excel 对接人清单导入数据库
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@Transactional
	@PostMapping("/importExcel")
	public Msg importExcel(@RequestParam MultipartFile file) throws Exception {
		byte[] fBytes = file.getBytes();
		InputStream fis = new ByteArrayInputStream(fBytes);
		Map<String, String> m = new HashMap<String, String>();
		m.put("编号", "id");
		m.put("姓名", "name");
		m.put("公司", "company");
		m.put("联系电话", "phone");
		m.put("背书公司名", "eCompany");
		m.put("背书账号", "eAccount");
		m.put("微信ID", "wechat");
		m.put("地址", "addr");
		m.put("地区", "area");
		List<Map<String, Object>> ls = ImportExcelTool.parseExcel(fis, file.getOriginalFilename(), m);
		String string = JSON.toJSONString(ls);
		if (string == null || string.equals("[]")) {
			msg.set("表格内容不能为空", CodeConstant.SET_ERR, null);
		} else {
			Staff staff = null;
			JSONArray platformList = JSON.parseArray(string);
			for (Object jsonObject : platformList) {
				staff = JSONObject.parseObject(jsonObject.toString(), Staff.class);
				staff = staffRepository.save(staff);
			}
			msg.set("导入成功", CodeConstant.SUCCESS, null);
		}
		return msg;
	}

	/**
	 * 分页查询对接人列表
	 * 
	 * @param index
	 *            开始位置
	 * @param size
	 *            列数
	 * @param name
	 *            对接人姓名
	 * @param company
	 *            公司名
	 * @return
	 */
	@PostMapping("/find")
	public Msg find(@SessionAttribute Admin admin, @RequestParam int index, @RequestParam int size,
			@RequestParam(required = false) String name, @RequestParam(required = false) String company) {
		Page<Staff> page = staffService.find(index, size, name, company);
		msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(page));
		return msg;
	}

	/**
	 * 查询指定的对接人 通过id
	 * 
	 * @param admin
	 * @param id
	 * @return
	 */
	@PostMapping("/id")
	public Msg findById(@RequestParam int id) {
		Staff staff = staffRepository.findOne(id);
		if (staff != null) {
			msg.set("查询成功", CodeConstant.SUCCESS, staff);
		} else {
			msg.set("查询失败", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}

	// 添加對接人
	@PostMapping("/add")
	public Msg add(@SessionAttribute Admin admin, @RequestParam String name, @RequestParam String company,
			@RequestParam String eAccount, @RequestParam String phone, @RequestParam(required = false) String addr) {
		if (!CheckTool.isPhone(phone)) {
			msg.set("手机号格式有误", CodeConstant.ERR_PAR, null);
			return msg;
		}

		Staff staff = new Staff(name, company, eAccount, phone, addr);
		staff = staffRepository.save(staff);
		if (staff != null) {
			msg.set("添加成功", CodeConstant.SUCCESS, staff);
		} else {
			msg.set("添加失败", CodeConstant.SET_ERR, null);
		}
		return msg;
	}

	/**
	 * 查詢所有對接人姓名
	 * 
	 * @return
	 */
	@PostMapping("/findStaffName")
	public List<Staff> findStaffName() {
		List<Staff> staffNamelist = staffRepository.findStaffName();
		return staffNamelist;
	}

	// 修改對接人
	@PostMapping("/set")
	public Msg set(@SessionAttribute Admin admin, @RequestParam int id, @RequestParam String name,
			@RequestParam String company, @RequestParam String eAccount, @RequestParam String phone,
			@RequestParam(required = false) String addr) {
		Staff staff = staffRepository.findOne(id);
		if (staff != null) {
			boolean isChange = false;
			if (!staff.getName().equals(name)) {
				isChange = true;
				staff.setName(name);
			}
			if (!staff.getCompany().equals(company)) {
				isChange = true;
				staff.setCompany(company);
			}
			if (!staff.getEAccount().equals(eAccount)) { // 背书账号
				isChange = true;
				staff.setName(name);
			}
			if (!staff.getPhone().equals(phone)) {
				isChange = true;
				staff.setPhone(phone);
			}
			if (CheckTool.isString(addr) && (staff.getAddr() == null || !staff.getAddr().equals(addr))) {
				isChange = true;
				staff.setAddr(addr);
			}
			if (isChange) {
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

	// 删除對接人
	@PostMapping("/del")
	public Msg del(@SessionAttribute Admin admin, @RequestParam int id) {
		Staff staff = staffRepository.findOne(id);
		List<Bill> billlist = billRepository.findByStaff(staff);
		for (Bill bill : billlist) {
			if (bill != null) {
				msg.set("对接人有对接清单，请先删除对应清单！", CodeConstant.SET_ERR, null);
				return msg;
			}
		}
		if (staff != null) {
			staffRepository.delete(staff);
			msg.set("删除成功", CodeConstant.SUCCESS, null);
		} else {
			msg.set("未找到此条信息", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}
}
