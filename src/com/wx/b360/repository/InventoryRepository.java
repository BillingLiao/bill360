package com.wx.b360.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.wx.b360.entity.Inventory;
import com.wx.b360.entity.UserByAdmin;



public interface InventoryRepository extends PagingAndSortingRepository<Inventory, Integer>, JpaSpecificationExecutor<Inventory> {
	
	//通过用户查找持票库存
	List<Inventory> findByUserByAdmin(UserByAdmin userByAdmin);
	
}
