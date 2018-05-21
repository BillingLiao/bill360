package com.wx.b360.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.wx.b360.entity.Acceptance;
import com.wx.b360.entity.Admin;
import com.wx.b360.entity.Card;
import com.wx.b360.entity.Inventory;
import com.wx.b360.entity.User;
import com.wx.b360.entity.UserByAdmin;
import com.wx.b360.model.Msg;
import com.wx.b360.tool.AppTool;
import com.wx.b360.tool.CheckTool;
import com.wx.b360.tool.CodeConstant;
import com.wx.b360.tool.FileTool;
import com.wx.b360.tool.HttpTool;

/**
 * 持票信息Controller
 * 
 * @author Billing
 *
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController extends BaseController {
	
	// 识别票据信息
	@PostMapping("/ocr")
	public Msg ocr(@RequestParam MultipartFile file) {

		String base64 = null;
		FileInputStream fileInputStream;
		try {
			fileInputStream = (FileInputStream) file.getInputStream();
			byte[] bytes = new byte[fileInputStream.available()];
			fileInputStream.read(bytes);
			base64 = java.util.Base64.getEncoder().encodeToString(bytes);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (CheckTool.isString(base64) && base64.length() > 21) {
			String url = "http://120.76.155.30:9191/pcc-ocr/api/recognize";
			Map<String, String> params = new HashMap<>();
			params.put("image_data", base64);
			String result = HttpTool.post(url, params);
			JSONObject jsonObject = JSONObject.parseObject(result);
			if (jsonObject.getInteger("code").intValue() == 200) {
				String recognize = jsonObject.getString("recognize_data");
				String endDate = null;
				String company = null;
				if (CheckTool.isString(recognize)) {
					jsonObject = JSONObject.parseObject(recognize);
					endDate = jsonObject.getString("endDate");
					company = jsonObject.getString("kpCompany");
					Map<String, String> data = new HashMap<>();
					data.put("endDate", endDate);
					data.put("company", company);
					data.put("ocr-result", result);

					msg.set("识别成功", CodeConstant.SUCCESS, data);
				} else {
					msg.set("识别失败:没有识别数据", CodeConstant.ERROR, null);
				}

			} else {
				msg.set("识别失败:" + jsonObject.getString("message"), CodeConstant.ERROR, null);
			}
		} else {
			msg.set("base64字串格式有误", CodeConstant.ERR_PAR, null);
		}
		return msg;
	}

	// 删除持票信息
	@PostMapping("/del")
	public Msg del(@SessionAttribute Admin admin, @RequestParam int id) {
		Inventory inventory = inventoryRepository.findOne(id);
		if (inventory != null) {
			inventoryRepository.delete(inventory);
			msg.set("删除成功", CodeConstant.SUCCESS, null);
		} else {
			msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}

	// 修改持票信息
	@PostMapping("/set")
	public Msg set(@SessionAttribute Admin admin, @RequestParam int id, @RequestParam(required = false) String company,
			@RequestParam(required = false) BigDecimal money, @RequestParam(required = false) String ctime,
			@RequestParam(required = false) String dtime, @RequestParam(required = false) String ttime,
			@RequestParam(required = false) String offer) {
		Inventory inventory = inventoryRepository.findOne(id);
		if (inventory != null) {
			boolean isChange = false;
			if (!inventory.getCompany().equals(company)) { // 判断与修改前数据是否不一致
				isChange = true;
				inventory.setCompany(company);
			}

			if (money.compareTo(inventory.getMoney()) != 0) {
				isChange = true;
				inventory.setMoney(money);
			}
			if (!inventory.getCtime().equals(ctime)) {
				System.out.println(ctime);
				isChange = true;
				inventory.setCtime(ctime);
			}
			if (!inventory.getDtime().equals(dtime)) {
				isChange = true;
				inventory.setDtime(dtime);
			}
			if (!inventory.getTtime().equals(ttime)) {
				isChange = true;
				inventory.setCtime(ttime);
			}
			if (!inventory.getOffer().equals(offer)) {
				isChange = true;
				inventory.setOffer(offer);
			}

			if (isChange) {
				inventory = inventoryRepository.save(inventory);
				msg.set("修改成功", CodeConstant.SUCCESS, inventory);
			} else {
				msg.set("没有要修改的数据", CodeConstant.ERROR, null);
			}
		} else {
			msg.set("未找到此条信息", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}

	// 获取指定持票信息
	@PostMapping("/id")
	public Msg findById(@SessionAttribute Admin admin, @RequestParam int id) {
		Inventory inventory = inventoryRepository.findOne(id);
		if (inventory != null) {
			msg.set("查询成功", CodeConstant.SUCCESS, inventory);
		} else {
			msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}

	// 获取指定持票信息(返回用户的名片)
	@PostMapping("/id/user")
	public Msg findByIdAndUserId(@SessionAttribute Admin admin, @RequestParam int id) {
		Inventory inventory = inventoryRepository.findOne(id);
		if (inventory != null) {
			User user = inventory.getUser();
			Card card = cardRepository.findCardByUser(user);
			if (card != null) {
				Map<String, Object> result = new HashMap<>();
				result.put("inventory", inventory);
				result.put("card", card);
				msg.set("查询成功", CodeConstant.SUCCESS_2, result);
			} else {
				msg.set("查询成功", CodeConstant.SUCCESS, inventory);
			}
		}else {
			msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}

	// 获取持票信息列表
	@PostMapping("/find")
	public Msg find(@RequestParam int index, @RequestParam int size, @RequestParam(required = false) String company) {
		Page<Inventory> page = inventoryService.find(index, size, null, null, company);
		msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(page));
		return msg;
	}

	// 获取当前用户的持票信息
	@PostMapping("/my")
	public Msg my(@SessionAttribute User user, @RequestParam int index, @RequestParam int size) {
		Page<Inventory> page = inventoryService.find(index, size, user, null, null);
		msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(page));
		return msg;
	}

	// 添加持票信息
	@PostMapping("/add")
	public Msg add(@RequestParam Integer userByAdminId, @RequestParam String company,
			@RequestParam MultipartFile file_img_front, @RequestParam(required = false) MultipartFile file_img_back,
			@RequestParam(required = false) BigDecimal money, @RequestParam(required = false) String ctime,
			@RequestParam(required = false) String dtime, @RequestParam(required = false) String ttime,
			@RequestParam(required = false) String offer, HttpServletRequest request) {

		String fileName_front;
		if (file_img_back != null) {
			fileName_front = UUID.randomUUID().toString() + System.currentTimeMillis()
					+ file_img_front.getOriginalFilename();
			String fileName_back = UUID.randomUUID().toString() + System.currentTimeMillis()
					+ file_img_back.getOriginalFilename();
			String filePath = request.getSession().getServletContext().getRealPath("/");
			try {
				FileTool.uploadFile(file_img_front.getBytes(), filePath, fileName_front);
				FileTool.uploadFile(file_img_back.getBytes(), filePath, fileName_back);
			} catch (Exception e) {
				// TODO: handle exception
				msg.set("图片上传失败", CodeConstant.SET_ERR, null);
			}
		} else {
			// String contentType = file_img_front.getContentType();
			fileName_front = UUID.randomUUID().toString() + System.currentTimeMillis()
					+ file_img_front.getOriginalFilename();
			System.out.println("fileName_back-->" + file_img_back);
			// System.out.println("getContentType-->" + contentType);
			String filePath = request.getSession().getServletContext().getRealPath("/");
			try {
				FileTool.uploadFile(file_img_front.getBytes(), filePath, fileName_front);
			} catch (Exception e) {
				// TODO: handle exception
				msg.set("图片上传失败", CodeConstant.SET_ERR, null);
			}
		}
		UserByAdmin userByAdmin = userByAdminRepository.findOne(userByAdminId);

		Inventory inventory = new Inventory(userByAdmin, company, fileName_front, null, money, ctime, dtime, ttime,
				offer);
		inventory = inventoryRepository.save(inventory);
		if (inventory != null) {
			// 判断是否有这个名称的承兑企业，如果没有则创建之
			Acceptance acceptance = acceptanceRepository.findByInvoice(company);
			if (acceptance == null) {
				acceptance = new Acceptance(company, null, null, null, 0, null, 0);
				acceptance = acceptanceRepository.save(acceptance);

				if (acceptance != null) {
					msg.set("添加成功", CodeConstant.SUCCESS, inventory);
				} else {
					msg.set("创建承兑企业时出错", CodeConstant.ERROR, null);
				}
			} else {
				// 已经存在承兑企业，不需要自动创建了
				msg.set("添加成功", CodeConstant.SUCCESS, inventory);
			}
		} else {
			msg.set("添加失败", CodeConstant.SET_ERR, null);
		}
		return msg;
	}

	@RequestMapping("/test")
	public Msg test() {
		msg.set("成功", CodeConstant.SUCCESS, this.getClass().getName());
		return msg;
	}

}
