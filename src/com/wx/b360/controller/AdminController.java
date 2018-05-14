package com.wx.b360.controller;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.wx.b360.entity.Admin;
import com.wx.b360.model.Msg;
import com.wx.b360.tool.CheckTool;
import com.wx.b360.tool.CodeConstant;
import com.wx.b360.tool.TokenTool;


@RestController
@RequestMapping("/admin")
public class AdminController extends BaseController {
	
	@PostMapping("/set")
	public Msg set(@SessionAttribute Admin admin, @RequestParam String oldPwd, 
			@RequestParam String newPwd) {
		boolean isChange = false;
		if(admin.getPwd().equals(oldPwd)) {
			admin.setPwd(newPwd);
			admin = adminRepository.save(admin);
			msg.set("修改成功", CodeConstant.SUCCESS, null);
		} else {
			msg.set("旧密码输入错误", CodeConstant.ERROR, null);
		}
		return msg;
	}
	
	@PostMapping("/login")
	public Msg login(@RequestParam String name, @RequestParam String pwd) {
		Admin admin = adminRepository.findByName(name);
		if(admin != null) {
			if(admin.getPwd().equals(pwd)) {
				String result = TokenTool.createA(admin.getId());
				msg.set("登录成功", CodeConstant.SUCCESS, result);
			} else {
				msg.set("用户名或密码错误", CodeConstant.ERROR, null);
			}
		} else {
			msg.set("用户名或密码错误", CodeConstant.ERROR, null);
		}
		return msg;
	}
	
	/**
	 * 登陆注销
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/logout")
	public String logout(HttpSession session)throws Exception{
		session.invalidate();
		return "redirect:/signin.html";
	}
	
	@RequestMapping("/test")
	public Msg test() {
		msg.set("成功", CodeConstant.SUCCESS, this.getClass().getName());
		return msg;
	}
}
