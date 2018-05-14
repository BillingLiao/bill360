package com.wx.b360.controller;

import java.util.List;

import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.wx.b360.entity.Admin;
import com.wx.b360.entity.Card;
import com.wx.b360.entity.User;
import com.wx.b360.model.Msg;
import com.wx.b360.tool.CheckTool;
import com.wx.b360.tool.CodeConstant;

@RestController
@RequestMapping("/card")
public class CardController extends BaseController {
	
	//删除名片
	@PostMapping("/del")
	public Msg del(@SessionAttribute User user, @RequestParam int id) {
		Card card = cardRepository.findOne(id);
		if(card != null) {
			if(user.getId() == card.getUser().getId()) {
				cardRepository.delete(card);
				msg.set("删除成功", CodeConstant.SUCCESS, null);
			} else {
				msg.set("您无权操作", CodeConstant.NO_AUTH, null);
			}
		} else {
			msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}
	
	//管理员查询某一用户的名片
	@PostMapping("/find") 
	public Msg find(@SessionAttribute Admin admin, @RequestParam int userId) {
		User user = userRepository.findOne(userId);
		if(user != null) {
			List<Card> cards = cardRepository.findByUser(user);
			msg.set("查询成功", CodeConstant.SUCCESS, cards);
		} else {
			msg.set("未找到此用户", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}
	
	//获取自己的名片
	@PostMapping("/my")
	public Msg my(@SessionAttribute User user) {
		List<Card> cards = cardRepository.findByUser(user);
		msg.set("查询成功", CodeConstant.SUCCESS, cards);
		return msg;
	}
	
	//修改用户名片
	@PostMapping("/set")
	public Msg set(@SessionAttribute User user, @RequestParam int id,
			@RequestParam(required=false) String name, @RequestParam(required=false) String addr,
			@RequestParam(required=false) String company, @RequestParam(required=false) String tel, 
			@RequestParam(required=false) String phone) {
		Card card = cardRepository.findOne(id);
		if(card != null) {
			if(card.getUser().getId() == user.getId()) {
				boolean isChange = false;
				if(CheckTool.isString(name) && !card.getName().equals(name)) {
					isChange = true;
					card.setName(name);
				}
				if(CheckTool.isString(addr) && !card.getAddr().equals(addr)) {
					isChange = true;
					card.setAddr(addr);
				}
				if(CheckTool.isString(company) && !card.getCompany().equals(company)) {
					isChange = true;
					card.setCompany(company);
				}
				if(CheckTool.isString(tel) && !card.getTel().equals(tel)) {
					isChange = true;
					card.setTel(tel);
				}
				if(CheckTool.isPhone(phone) && !card.getPhone().equals(phone)) {
					isChange = true;
					card.setPhone(phone);
				}
				if(isChange) {
					card = cardRepository.save(card);
					msg.set("修改成功", CodeConstant.SUCCESS, card);
				} else {
					msg.set("没有要修改的数据", CodeConstant.ERROR, null);
				}
			} else {
				msg.set("您无权操作", CodeConstant.NO_AUTH, null);
			}
		} else {
			msg.set("未找到此条数据", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}
	
	//添加用户的名片
	@PostMapping("/add")
	public Msg add(@SessionAttribute User user, @RequestParam String name, @RequestParam(required=false) String addr,
			@RequestParam(required=false) String company, @RequestParam(required=false) String tel, 
			@RequestParam(required=false) String phone, @RequestParam(required=false) String duty) {
		//判断用户是否已经有了名片
		List<Card> cards = cardRepository.findByUser(user);
		if(cards != null && cards.size() > 0) {
			msg.set("您已经有名片了", CodeConstant.ERROR, null);
		} else {
			if(CheckTool.isString(phone) && !CheckTool.isPhone(phone)) {
				msg.set("手机号格式有误", CodeConstant.ERR_PAR, null);
			} else {
				Card card = new Card(user, name, addr, company, tel, phone, duty);
				card = cardRepository.save(card);
				
				user.setCard(1);
				userRepository.save(user);
				ValueOperations<String, User> operations=redisTemplate.opsForValue();
				operations.set("user:"+user.getId(), user);
				
				msg.set("添加成功", CodeConstant.SUCCESS, card);
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
