package com.wx.b360.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.wx.b360.entity.Bill;


public interface BillRepository extends PagingAndSortingRepository<Bill, Integer>, JpaSpecificationExecutor<Bill> {

	List<Bill> findByCore(String core);

}
