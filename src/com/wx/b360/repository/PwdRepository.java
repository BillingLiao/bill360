package com.wx.b360.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.wx.b360.entity.Pwd;
import com.wx.b360.entity.User;


public interface PwdRepository extends PagingAndSortingRepository<Pwd, Integer> {

	Pwd findByOpenid(String openid);

	Pwd findByPhone(String phone);

	Pwd findByUser(User user);

}
