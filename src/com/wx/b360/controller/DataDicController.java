package com.wx.b360.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.wx.b360.entity.Admin;
import com.wx.b360.entity.DataDic;
import com.wx.b360.entity.Inventory;
import com.wx.b360.entity.UserByAdmin;
import com.wx.b360.model.Msg;
import com.wx.b360.tool.AppTool;
import com.wx.b360.tool.CodeConstant;

/**
 * 管理员建用户名Controller
 * 
 * @author Billing
 *
 */
@RestController
@RequestMapping("/dataDic")
public class DataDicController extends BaseController {

	// 删除数据字典
	@PostMapping("/del")
	public Msg del(@SessionAttribute Admin admin, @RequestParam int id) {
		DataDic dataDic = dataDicRepository.findOne(id);
		if (dataDic != null) {
			dataDicRepository.delete(dataDic);
			msg.set("删除成功", CodeConstant.SUCCESS, null);
		} else {
			msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}

	// 获取指定的数据字典
	@PostMapping("/id")
	public Msg findById(@SessionAttribute Admin admin, @RequestParam int id) {
		DataDic dataDic = dataDicRepository.findOne(id);
		if (dataDic != null) {
			msg.set("查询成功", CodeConstant.SUCCESS, dataDic);
		} else {
			msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}

	// 修改数据字典
	@PostMapping("/set")
	public Msg set(@SessionAttribute Admin admin, @RequestParam int id, @RequestParam String dataDicName,
			@RequestParam String dataDicValue) {
		DataDic dataDic = dataDicRepository.findOne(id);
		if (dataDic != null) {
			boolean isChange = false;
			if (!dataDic.getDataDicName().equals(dataDicName)) { // 判断与修改前数据是否不一致
				isChange = true;
				dataDic.setDataDicName(dataDicName);
			}
			if (!dataDic.getDataDicValue().equals(dataDicValue)) { // 判断与修改前数据是否不一致
				isChange = true;
				dataDic.setDataDicValue(dataDicValue);
			}

			if (isChange) {
				dataDic = dataDicRepository.save(dataDic);
				msg.set("修改成功", CodeConstant.SUCCESS, dataDic);
			} else {
				msg.set("没有要修改的数据", CodeConstant.ERROR, null);
			}
		} else {
			msg.set("未找到此条信息", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}

	// 获取数据字典列表
	@PostMapping("/find")
	public Msg find(@SessionAttribute Admin admin, @RequestParam int index, @RequestParam int size,
			@RequestParam(required = false) String dataDicName, @RequestParam(required = false) String dataDicValue) {
		Page<DataDic> page = dataDicService.find(index, size, dataDicName, dataDicValue);
		msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(page));
		return msg;
	}

	/**
	 * 查询数据字典名列表
	 * 
	 * @return
	 */
	@PostMapping("/findDataDicName")
	public Msg findDataDicName(@SessionAttribute Admin admin) {
		List<String> DataDicNamelist = dataDicRepository.findDataDicName();
		msg.set("查询成功", CodeConstant.SUCCESS, DataDicNamelist);
		return msg;
	}

	/**
	 * 根据数据字典名查数据字典值
	 * 
	 * @return
	 */
	@PostMapping("/findDataDicValue")
	public List<DataDic> findDataDicValueByName(@SessionAttribute Admin admin, @RequestParam String data_Dic_Name) {
		List<DataDic> dataDicNamelist = dataDicRepository.findDataDicValueByName(data_Dic_Name);
		return dataDicNamelist;
	}

	// 添加数据字典
	@PostMapping("/add")
	public Msg add(@SessionAttribute Admin admin, @RequestParam String dataDicName, @RequestParam String dataDicValue) {

		DataDic dataDic = dataDicRepository.findByDataDicValue(dataDicValue);

		// 不能有相同数据
		if (dataDic == null) {
			dataDic = new DataDic(dataDicName, dataDicValue);
			dataDic = dataDicRepository.save(dataDic);
			msg.set("添加成功", CodeConstant.SUCCESS, dataDic);
		} else {
			if (!dataDic.getDataDicName().equals(dataDicName)) {
				dataDic = new DataDic(dataDicName, dataDicValue);
				dataDic = dataDicRepository.save(dataDic);
				msg.set("添加成功", CodeConstant.SUCCESS, dataDic);
			} else {
				msg.set("该项数据已创建", CodeConstant.SET_ERR, null);
			}
		}
		return msg;
	}

	@RequestMapping("/test")
	public Msg test() {
		msg.set("成功", CodeConstant.SUCCESS, this.getClass().getName());
		return msg;
	}

}
