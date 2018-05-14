package com.wx.b360.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.wx.b360.entity.User;
import com.wx.b360.entity.UserByAdmin;

public interface UserByAdminRepository extends PagingAndSortingRepository<UserByAdmin, Integer>, JpaSpecificationExecutor<UserByAdmin> {
	
	@Query(value="select * from b_userbyadmin ORDER BY id", nativeQuery=true)
	public List<UserByAdmin> findUserName();
}
