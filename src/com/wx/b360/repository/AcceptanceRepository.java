package com.wx.b360.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.wx.b360.entity.Acceptance;

public interface AcceptanceRepository extends PagingAndSortingRepository<Acceptance, Integer>, JpaSpecificationExecutor<Acceptance>{

	Acceptance findByInvoice(String invoice);
	
	@Query(value="select * from b_acceptance where _core = ?1 order by _id", nativeQuery=true)
	public List<Acceptance> findInvoiceByCore(String core);
	
	//通过core主体模糊查询 所有core
	@Query(value="select distinct _core from b_acceptance where _core like %?1% order by _id", nativeQuery=true)
	public List<String> findAllCoreByCore(String core);
	
	//查询 所有core
	@Query(value="select distinct _core from b_acceptance", nativeQuery=true)
	public List<String> findCore();
	
	//通过core跟invoice查询一条信息
	@Query(value="select * from b_acceptance where _core = ?1 and _invoice = ?2", nativeQuery=true)
	public Acceptance findByCoreAndInvoice(String core,String invoice);
	
	//通过core跟invoice,type 查询一条信息
	@Query(value="select * from b_acceptance where _core = ?1 and _invoice = ?2 and _type = ?3", nativeQuery=true)
	public Acceptance findByCoreAndInvoiceAndType(String core,String invoice,Integer type);
}
