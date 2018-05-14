package com.wx.b360.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.wx.b360.entity.DataDic;

public interface DataDicRepository extends PagingAndSortingRepository<DataDic, Integer>, JpaSpecificationExecutor<DataDic> {
	
	@Query(value="select distinct data_Dic_Name from b_datadic order by id", nativeQuery=true)
	public List<String> findDataDicName();
	
	@Query(value="select * from b_datadic where data_Dic_Name = ?1 order by id", nativeQuery=true)
	public List<DataDic> findDataDicValueByName(String data_Dic_Name);
	
	DataDic findByDataDicName(String dataDicName);
	
	DataDic findByDataDicValue(String dataDicValue);
}
