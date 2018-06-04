package com.wx.b360.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.wx.b360.entity.Acceptance;
import com.wx.b360.entity.Bill;
import com.wx.b360.entity.Staff;


public interface BillRepository extends PagingAndSortingRepository<Bill, Integer>, JpaSpecificationExecutor<Bill> {

	//List<Bill> findByCore(String core);
	
	List<Bill> findByAcceptance(Acceptance acceptance);
	
	List<Bill> findByStaff(Staff staff);
	
	@Query(value="select * from b_bill where _staff_id = ?1 and _acceptance_id =?2", nativeQuery=true)
	Bill findByStaffAndAcceptance(int staffId,int acceptanceId);
	
	//通过核心企业名 查找利率最小的一条 bill
	@Query(value="SELECT * FROM b_bill WHERE _rate IN ( SELECT MIN(b.`_rate`) FROM b_bill b LEFT JOIN b_acceptance a ON b.`_acceptance_id` = a.`_id` WHERE a.`_core` = ?1)", nativeQuery=true)
	Bill findMinRateBycore(String core);
	
	
	//通过核心企业名 查找 优先级最高的一条  bill
	@Query(value = "SELECT * FROM  b_bill b LEFT JOIN b_acceptance a ON b.`_acceptance_id` = a.`_id` WHERE a.`_core` = ?1  ORDER BY _level DESC  LIMIT 0,1", nativeQuery = true)
	Bill fastByCore(String core);
	
	//通过核心企业，开票企业，类别 查找 bill
	//@Query(value="SELECT b.* FROM b_bill b LEFT JOIN b_acceptance a ON b._acceptance_id = a._id  WHERE a._core = ?1 and a._invoice = ?2 and a._type = ?3", nativeQuery=true)
	//List<Bill> findByCoreInvoiceType(String core,String invoice, Integer type);
	
}
