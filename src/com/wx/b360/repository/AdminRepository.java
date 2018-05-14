package com.wx.b360.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.wx.b360.entity.Admin;


public interface AdminRepository extends PagingAndSortingRepository<Admin, Integer> {

	Admin findByName(String name);

}
