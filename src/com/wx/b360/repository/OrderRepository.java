package com.wx.b360.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.wx.b360.entity.Order;


public interface OrderRepository extends PagingAndSortingRepository<Order, Integer>, JpaSpecificationExecutor<Order> {


}
