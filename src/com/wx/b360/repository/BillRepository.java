package com.wx.b360.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.wx.b360.entity.Acceptance;
import com.wx.b360.entity.Bill;
import com.wx.b360.entity.Staff;


public interface BillRepository extends PagingAndSortingRepository<Bill, Integer>, JpaSpecificationExecutor<Bill> {

	//List<Bill> findByCore(String core);
	
	List<Bill> findByAcceptance(Acceptance acceptance);
	
	List<Bill> findByStaff(Staff staff);

}
