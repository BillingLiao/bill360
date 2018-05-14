package com.wx.b360.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.wx.b360.model.Msg;
import com.wx.b360.repository.AcceptanceRepository;
import com.wx.b360.repository.AdminRepository;
import com.wx.b360.repository.BillRepository;
import com.wx.b360.repository.CardRepository;
import com.wx.b360.repository.ConstantRepository;
import com.wx.b360.repository.DataDicRepository;
import com.wx.b360.repository.InventoryRepository;
import com.wx.b360.repository.OrderRepository;
import com.wx.b360.repository.PwdRepository;
import com.wx.b360.repository.RecordRepository;
import com.wx.b360.repository.SourceRepository;
import com.wx.b360.repository.StaffRepository;
import com.wx.b360.repository.UserByAdminRepository;
import com.wx.b360.repository.UserRepository;
import com.wx.b360.service.AcceptanceService;
import com.wx.b360.service.BillService;
import com.wx.b360.service.CardService;
import com.wx.b360.service.DataDicService;
import com.wx.b360.service.InventoryService;
import com.wx.b360.service.OrderService;
import com.wx.b360.service.RecordService;
import com.wx.b360.service.SourceService;
import com.wx.b360.service.UserByAdminService;
import com.wx.b360.service.UserService;


public class BaseController {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@SuppressWarnings("rawtypes")
	@Autowired protected RedisTemplate redisTemplate;
	
	@Autowired protected Msg msg;
	
	@Autowired protected AdminRepository adminRepository;
	@Autowired protected BillRepository billRepository;
	@Autowired protected CardRepository cardRepository;
	@Autowired protected PwdRepository pwdRepository;
	@Autowired protected RecordRepository recordRepository;
	@Autowired protected SourceRepository sourceRepository;
	@Autowired protected UserRepository userRepository;
	@Autowired protected OrderRepository orderRepository;
	@Autowired protected ConstantRepository constantRepository;
	@Autowired protected StaffRepository staffRepository;
	@Autowired protected AcceptanceRepository acceptanceRepository;
	@Autowired protected InventoryRepository inventoryRepository;
	@Autowired protected UserByAdminRepository userByAdminRepository;
	@Autowired protected DataDicRepository  dataDicRepository;
	
	@Autowired protected BillService billService;
	@Autowired protected CardService cardService;
	@Autowired protected RecordService recordService;
	@Autowired protected SourceService sourceService;
	@Autowired protected UserService userService;
	@Autowired protected OrderService orderService;
	@Autowired protected AcceptanceService acceptanceService;
	@Autowired protected InventoryService inventoryService;
	@Autowired protected UserByAdminService userByAdminService;
	@Autowired protected DataDicService dataDicService;
}
