package com.wx.b360.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.wx.b360.entity.Acceptance;


public interface AcceptanceRepository extends PagingAndSortingRepository<Acceptance, Integer>, JpaSpecificationExecutor<Acceptance>{

	Acceptance findByInvoice(String invoice);
}
