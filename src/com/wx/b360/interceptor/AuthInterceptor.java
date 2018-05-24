package com.wx.b360.interceptor;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.wx.b360.entity.Admin;
import com.wx.b360.entity.User;
import com.wx.b360.model.Msg;
import com.wx.b360.repository.AdminRepository;
import com.wx.b360.repository.UserRepository;
import com.wx.b360.tool.CheckTool;
import com.wx.b360.tool.CodeConstant;
import com.wx.b360.tool.HttpTool;
import com.wx.b360.tool.TokenTool;


/**
 * 权限拦截器
 * @author 莫小雨
 *
 */
public class AuthInterceptor implements HandlerInterceptor {

	@Autowired
	private Msg msg;
	
	@Autowired private UserRepository userRepository;
	@Autowired private AdminRepository adminRepository;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String token = request.getParameter("token");
		String atoken = request.getParameter("atoken");
		
		if(CheckTool.isString(token)) {
			int userId = TokenTool.verify(token);
			ValueOperations<String, User> operations=redisTemplate.opsForValue();
			boolean exists = redisTemplate.hasKey("user:"+userId);
			if(exists) {
				//有缓存的情况，直接从缓存中取
				User user = operations.get("user:" + userId);
				if(user.getStatus() == 1) {
					//被锁定的情况，从数据库中确认
					user = userRepository.findOne(userId);
					operations.set("user:"+userId, user);
					msg.set("您被管理员锁定，请联系管理员处理", CodeConstant.LOCK, null);
				} else {
					request.getSession().setAttribute("user", user);
					return true;
				}
			} else {
				//没有缓存的情况，从数据库中取
				if(userId > 0) {
					User user = userRepository.findOne(userId);
					if(user != null) {
						
						if(user.getStatus() == 1) {
							//被锁定的情况
							msg.set("您被管理员锁定，请联系管理员处理", CodeConstant.LOCK, null);
						} else {
							request.getSession().setAttribute("user", user);
							operations.set("user:"+userId, user);
							return true;
						}
					} else {
						msg.set("没有找到指定用户", CodeConstant.ERR_AUTH, null);
					}
				} else if(userId == 0) {
					msg.set("token超时，请重新登录", CodeConstant.ERR_AUTH, null);
				} else {
					msg.set("token有误", CodeConstant.ERR_AUTH, null);
				}
			}
		} else if(CheckTool.isString(atoken)) {
			int adminId = TokenTool.verifyA(atoken);
			ValueOperations<String, Admin> operations=redisTemplate.opsForValue();
			boolean exists = redisTemplate.hasKey("admin:"+adminId);
			if(exists) {
				//有缓存的情况，直接从缓存中取
				Admin admin = operations.get("admin:"+adminId);
				request.getSession().setAttribute("admin", admin);
				return true;
			} else {
				//没有缓存的情况，从数据库中取
				if(adminId > 0) {
					Admin admin = adminRepository.findOne(adminId);
					if(admin != null) {
						request.getSession().setAttribute("admin", admin);
						operations.set("admin:"+adminId, admin);
						return true;
					} else {
						msg.set("没有找到指定管理员", CodeConstant.ERR_AUTH, null);
					}
				} else if(adminId == 0) {
					msg.set("atoken超时，请重新登录", CodeConstant.ERR_AUTH, null);
				} else {
					msg.set("atoken有误", CodeConstant.ERR_AUTH, null);
				}
			}
		} else {
			//没有带token的情况
			msg.set("请先登录", CodeConstant.ERR_AUTH, null);
		}
		HttpTool.jsonMsg(response, msg);
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if(response.getStatus() == 200) {
			//访问正常的情况
			return ;
		} else if(response.getStatus() == 500) {
			msg.set("Server Error by wenxi", CodeConstant.ERROR, null);
		} else if(response.getStatus() == 404) {
			msg.set("URL Error by wenxi", CodeConstant.ERROR, null);
		} else {
			msg.set("Nuknow Error:" + response.getStatus() + " by wenxi", CodeConstant.ERROR, null);
		}
//		modelAndView.addObject(msg);
//		HttpTool.jsonMsg(response, msg);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
	
}
