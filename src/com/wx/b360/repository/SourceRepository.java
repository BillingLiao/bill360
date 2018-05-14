package com.wx.b360.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.wx.b360.entity.Source;


public interface SourceRepository extends PagingAndSortingRepository<Source, Integer>, JpaSpecificationExecutor<Source> {

	Source findByInvoice(String invoice);

}
