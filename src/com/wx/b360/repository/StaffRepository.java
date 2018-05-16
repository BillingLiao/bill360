package com.wx.b360.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.wx.b360.entity.Staff;

public interface StaffRepository extends PagingAndSortingRepository<Staff, Integer> {
	
	@Query(value="select * from b_staff ORDER BY _id", nativeQuery=true)
	public List<Staff> findStaffName();

}
