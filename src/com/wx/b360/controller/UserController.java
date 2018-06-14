package com.wx.b360.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import com.wx.b360.entity.Admin;
import com.wx.b360.entity.Pwd;
import com.wx.b360.entity.User;
import com.wx.b360.model.Code;
import com.wx.b360.model.Msg;
import com.wx.b360.service.UserService;
import com.wx.b360.tool.AppTool;
import com.wx.b360.tool.CheckTool;
import com.wx.b360.tool.CodeConstant;
import com.wx.b360.tool.FileTool;
import com.wx.b360.tool.HttpTool;
import com.wx.b360.tool.TokenTool;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

	private final long MAX_TIME = 60000;	//获取验证码的时间间隔
	
	//验证码有效时间（毫秒）
	private final long MAX_CODE_TIME = 900000;

	//阿里大于APPKEY
	private final String APP_KEY = "24776373";
	//阿里大于APPSECRET
	private final String APP_SECRET = "a9043d7763a047ae0ce642deaa50b92f";
	
	//微信小程序appid
	private final String WX_APP_ID = "wxef57e6a7c3c58536";
	
	//微信小程序appsecret
	private final String WX_APP_SECRET = "7d01d6ede415010f90c15c652d583102";
	
	//private final String WX_APP_ID = "wxc14f10b94e87d428";
	//private final String WX_APP_SECRET = "0c4c51775857e72cc299845d5de4e09b";
	
	//获取微信openid
	//获取openid
	@PostMapping("/openid")
	public  Msg getOpenid(@RequestParam  String code) {
		String str = HttpTool.get("https://api.weixin.qq.com/sns/jscode2session?appid="+WX_APP_ID+"&secret="+WX_APP_SECRET+"&grant_type=authorization_code&js_code=" + code);
		System.out.println("获取微信openid结果");
		System.out.println(str);
		try {
//			String accessToken = JSON.parseObject(str).getString("access_token");
			String openId = JSON.parseObject(str).getString("openid");
			if(CheckTool.isString(openId)) {
				msg.set("获取openid成功", CodeConstant.SUCCESS, openId);
			} else {
				msg.set("openid获取失败，可能是code重复使用", CodeConstant.ERROR, null);
			}
			
			
//			String userInfo = HttpTool.get("https://api.weixin.qq.com/sns/userinfo?access_token="+accessToken+"&openid="+openId);
			
			
		} catch(Exception e) {
			msg.set("获取微信信息失败", CodeConstant.ERROR, null);
		}
		
		return msg;

	}
	
	//发送验证码
	@PostMapping("/code") 
	public Msg code(@SessionAttribute User user, @RequestParam String phone) {
		if(CheckTool.isPhone(phone)) {
			ValueOperations<String, Code> operations=redisTemplate.opsForValue();
			boolean exist = redisTemplate.hasKey(phone);
			if(exist && (System.currentTimeMillis() - operations.get(phone).getTime().getTime()) < MAX_TIME) {
				//有缓存并且时间间隔过短
				msg.set("获取验证码太频繁，请等一分钟再来哦", CodeConstant.ERROR, null);
			} else {
				//需要发送验证码的情况
				TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", APP_KEY, APP_SECRET);
				AlibabaAliqinFcSmsNumSendRequest request = new AlibabaAliqinFcSmsNumSendRequest();
				request.setSmsType("normal");
				request.setSmsFreeSignName("查票360");
				request.setRecNum(phone);
				int random = (int) (100000 + (899999*Math.random()));
				request.setSmsParamString("{\"code\":\""+random+"\"}");
				request.setSmsTemplateCode("SMS_121915006");
				try {
					AlibabaAliqinFcSmsNumSendResponse response = client.execute(request);
					logger.info("阿里大于返回内容:"+response.getBody());
					if(response.isSuccess()) {
						msg.set("发送成功", CodeConstant.SUCCESS, null);

						//将新验证码添加/覆盖
						Code code = new Code(random+"", new Timestamp(System.currentTimeMillis()), phone);
						operations.set(phone, code);
					} else {
						msg.set("验证码发送失败,请稍候再试", CodeConstant.ERROR, null);
					}
				} catch (Exception e) {
					logger.info("错误信息:"+e);
					msg.set("验证码发送失败,请稍候再试", CodeConstant.ERROR, null);
				}
			}
		} else {
			msg.set("请输入正确的手机号", CodeConstant.ERR_PAR, null);
		}
		return msg;
	}
	
	//绑定/换绑手机号
	@PostMapping("/phone")
	public Msg phone(@SessionAttribute User user, @RequestParam String phone, @RequestParam String code) {
		if(CheckTool.isPhone(phone)) {
			//判断验证码是否正确
			ValueOperations<String, Code> operations=redisTemplate.opsForValue();
			boolean exist = redisTemplate.hasKey(phone);
			if(exist) {
				Code codeEntity = operations.get(phone);
				if(System.currentTimeMillis() - codeEntity.getTime().getTime() < MAX_CODE_TIME) {
					if(code.equals(codeEntity.getContent())) {
						//验证码无误且有效，判断此手机号是否被占用
						Pwd pwd = pwdRepository.findByPhone(phone);
						//如果此手机号和当前用户相同，说明此用户要修改的手机号是老手机号，不允许修改
						if(pwd != null) {
							if(pwd.getUser().getId() == user.getId()) {
								msg.set("新旧手机号不能相同", CodeConstant.ERROR, null);
							} else {
								msg.set("此手机已被占用", CodeConstant.ERROR, null);
							}
						} else {
							pwd = pwdRepository.findByUser(user);
							pwd.setPhone(phone);
							pwd = pwdRepository.save(pwd);
							
							user.setType(1);
							userRepository.save(user);
							
							msg.set("绑定成功", CodeConstant.SUCCESS, null);
						}
					} else {
						msg.set("验证码有误", CodeConstant.ERROR, null);
					}
				} else {
					msg.set("验证码已过期，请重新获取", CodeConstant.ERROR, null);
				}
			} else {
				msg.set("请先获取验证码", CodeConstant.ERROR, null);
			}
		} else {
			msg.set("手机号格式有误", CodeConstant.ERR_PAR, null);
		}
		return msg;
	}
	
	//获取指定用户的信息
	@PostMapping(value="/id")
	public Msg findById(@RequestParam int id, @RequestParam(required=false) String atoken) {
		User user = userRepository.findOne(id);
		if(user != null) {
			msg.set("查询成功", CodeConstant.SUCCESS, user);
		} else {
			msg.set("未找到此用户", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}
	
	//统计用户信息
	@PostMapping(value="/stat")
	public Msg stat() {
		ArrayList<Date> bound = AppTool.getTodayBound();
		Page<User> page = userService.find(0, 1, null,null,bound.get(0),bound.get(1),null);
		long todayNum = page.getTotalElements();
		long totalNum = userRepository.findAll(new PageRequest(0, 1)).getTotalElements();

		Map<String, Long> result = new HashMap<>();
		result.put("todayNum", todayNum);
		result.put("totalNum", totalNum);

		msg.set("查询成功", CodeConstant.SUCCESS, result);
		return msg;
	}
	
	//管理员锁定/解锁用户
	@PostMapping("lock")
	public Msg lock(@SessionAttribute Admin admin, @RequestParam int id) {
		User user = userRepository.findOne(id);
		if(user != null) {
			if(user.getStatus() == 0) {
				//锁定
				user.setStatus(1);
			} else {
				//解锁
				user.setStatus(0);
			}
			userRepository.save(user);
			//更新缓存
			ValueOperations<String, User> operations=redisTemplate.opsForValue();
			operations.set(""+user.getId(), user);
			msg.set("操作成功", CodeConstant.SUCCESS, user);
		} else {
			msg.set("", CodeConstant.FIND_ERR, null);
		}
		return msg;
	}
	
	//管理员获取用户列表
	@PostMapping("/find")
	public Msg find(@SessionAttribute Admin admin, @RequestParam int index, @RequestParam int size, 
			@RequestParam(required=false) Integer status, @RequestParam(required=false) Integer type,
			@RequestParam(required=false) String start, @RequestParam(required=false) String stop,
			@RequestParam(required=false) String name) {
		Date startDate = null, stopDate = null;
		if(CheckTool.isDate(start)) startDate = AppTool.changeDate(start);
		if(CheckTool.isDate(stop)) stopDate =  AppTool.changeDate(stop);
		Page<User> page = userService.find(index, size, status, type,startDate,stopDate,"%"+name+"%");
		msg.set("查询成功", CodeConstant.SUCCESS, AppTool.pageToMap(page));
		return msg;
	}
	
	/**
	 * 查询所有用户信息
	 * @return
	 */
	@PostMapping("/findUserName")
	public List<User> findUserName(){
		List<User> userNamelist = userRepository.findUserName();
		return userNamelist;
	}
	
	//图片上传
	@PostMapping("/upload")
	public Msg upload(@RequestParam String base64, HttpServletRequest request) {
		String result = FileTool.upload(request, base64);
		if(CheckTool.isString(result)) {
			msg.set("上传成功", CodeConstant.SUCCESS, result);
		} else {
			msg.set("上传失败", CodeConstant.ERROR, null);
		}
		return msg;
	}
	
	//upload上传图片---微信小程序端
	@RequestMapping("/img")
	public String upload(@RequestParam MultipartFile file, HttpServletRequest request) {
		String contentType = file.getContentType();
		String fileName = UUID.randomUUID().toString()+System.currentTimeMillis()+file.getOriginalFilename();
		/*System.out.println("fileName-->" + fileName);
	        System.out.println("getContentType-->" + contentType);*/
		String filePath = request.getSession().getServletContext().getRealPath("/");
		try {
			FileTool.uploadFile(file.getBytes(), filePath, fileName);
			return fileName;
		} catch (Exception e) {
			// TODO: handle exception
			return "上传失败";
		}
	}
	
	//用户修改基本信息
	@PostMapping("/set")
	public Msg set(@SessionAttribute User user, @RequestParam(required=false) String name,
			@RequestParam(required=false) String head) {
		boolean isChange = false;
		if(CheckTool.isString(name) && !user.getName().equals(name)) {
			isChange = true;
			user.setName(name);
		}
		if(CheckTool.isString(head) && !user.getHead().equals(head)) {
			isChange = true;
			user.setHead(head);
		}
		if(isChange) {
			user = userRepository.save(user);
			ValueOperations<String, User> operations=redisTemplate.opsForValue();
			operations.set("user:"+user.getId(), user);
			msg.set("修改成功", CodeConstant.SUCCESS, user);
		} else {
			msg.set("没有需要修改的数据", CodeConstant.ERROR, null);
		}
		return msg;
	}
	
	//用户登录
	@PostMapping("/login")
	public Msg login(@RequestParam String openid, HttpServletRequest request,
			@RequestParam(required=false) String head, @RequestParam(required=false) String name) {
		Pwd pwd = pwdRepository.findByOpenid(openid); 
		if(pwd != null) {
			//老用户
			User user = pwd.getUser();
			String token = TokenTool.create(user.getId());
			Map<String, Object> result = new HashMap<>();
			user.setPhone(pwd.getPhone());
			result.put("user", user);
			result.put("token", token);
			
			msg.set("登录成功", CodeConstant.SUCCESS, result);
		} else {
			//新用户
			if(CheckTool.isString(head)) {
				head = FileTool.uploadQianURL(request, head);
			}
			if(!CheckTool.isString(head)) {
				head = "user-head.png";
			}
			if(!CheckTool.isString(name)) name = "查票360";
			User user = new User(head, name);
			user = userRepository.save(user);
			pwd = new Pwd(user, openid);
			pwd = pwdRepository.save(pwd);
			
			String token = TokenTool.create(user.getId());
			Map<String, Object> result = new HashMap<>();
			result.put("user", user);
			result.put("token", token);
			
			msg.set("登录成功", CodeConstant.SUCCESS, result);
		}
		return msg;
	}
	
	/**
	 * 获取当前登录用户基本信息并刷新缓存
	 * @param user
	 * @return
	 */
	@PostMapping("/info")
	public Msg info(@SessionAttribute User user) {
		user = userRepository.findOne(user.getId());
		ValueOperations<String, User> operations=redisTemplate.opsForValue();
		operations.set("user:"+user.getId(), user);
		Pwd pwd = pwdRepository.findByUser(user);
		user.setPhone(pwd.getPhone());
		msg.set("查询成功", CodeConstant.SUCCESS, user);
		return msg;
	}
	
	@RequestMapping("/test")
	public Msg test() {
		msg.set("成功", CodeConstant.SUCCESS, this.getClass().getName());
		return msg;
	}
}
