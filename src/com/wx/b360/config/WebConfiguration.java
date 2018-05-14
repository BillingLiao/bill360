package com.wx.b360.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.wx.b360.interceptor.AuthInterceptor;
import com.wx.b360.interceptor.LogInterceptor;


@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter {
	
	//放行接口
	private final String[] noInterceptor = {
		"/user/login",	//用户登录
		"/admin/login",	//管理员登录
		"/user/openid",	//获取openid
		"/bill/ocr",	//票据识别
		"/user/img",	//上傳圖片、無需token-2018年3月16日蘇在微信中要求的
		"/order/calc", //计算计息天数、每十万贴息和利息
	};
	
	@Bean
	public AuthInterceptor authInterceptor() {
		return new AuthInterceptor();
	}
	@Bean
	public LogInterceptor logInterceptor() {
		return new LogInterceptor();
	}
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(logInterceptor()).addPathPatterns("/**");
		registry.addInterceptor(authInterceptor()).addPathPatterns("/**")
		//不过滤的路径-不过滤结尾为test的接口
		.excludePathPatterns(noInterceptor).excludePathPatterns("/**/test");
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		//防止跨域
		registry.addMapping("/**").allowedOrigins("*").allowedHeaders("*").allowedMethods("*");
	} 
}
