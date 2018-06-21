package com.wx.b360.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.wx.b360.entity.Record;


public interface RecordRepository extends PagingAndSortingRepository<Record, Integer>, JpaSpecificationExecutor<Record> {

	@Query(value="select r._word as word, count(r._word) as num from b_record as r GROUP BY r._word ORDER BY num DESC LIMIT 0,5", nativeQuery=true)
	List<Object> rank();
	
}
