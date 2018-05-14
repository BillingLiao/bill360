package com.wx.b360.repository;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.wx.b360.entity.Inventory;



public interface InventoryRepository extends PagingAndSortingRepository<Inventory, Integer>, JpaSpecificationExecutor<Inventory> {
	
}
