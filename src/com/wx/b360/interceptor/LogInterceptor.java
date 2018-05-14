package com.wx.b360.interceptor;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.wx.b360.tool.TokenTool;



public class LogInterceptor implements HandlerInterceptor {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	//项目名
	private final String PROJECT_NAME = "查票360";
	
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		logger.info(PROJECT_NAME+"-***********************");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(PROJECT_NAME+"-" + format.format(new Date()));
		logger.info(PROJECT_NAME+"-" + request.getServletPath());
		Enumeration<String> params = request.getParameterNames();
		while(params.hasMoreElements()) {
			String param = params.nextElement();
			if(!param.equals("base64")) {
				logger.info("    "+PROJECT_NAME+"-" + param + ":" + request.getParameter(param));
				if(param.equals("token")) {
					String token = request.getParameter(param);
					logger.info("    "+ PROJECT_NAME+"-"+"用户id:" + TokenTool.verify(token));
				}
			}
		}
		
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
	
}
