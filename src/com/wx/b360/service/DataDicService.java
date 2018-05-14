package com.wx.b360.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.wx.b360.entity.DataDic;
import com.wx.b360.repository.DataDicRepository;

import com.wx.b360.tool.CheckTool;

@Service
public class DataDicService {
	@Resource DataDicRepository dataDicRepository;

	public Page<DataDic> find(int index, int size, String dataDicName, String dataDicValue) {
		// TODO Auto-generated method stub
		Specification<DataDic> specification = new Specification<DataDic>() {
			
			@Override
			public Predicate toPredicate(Root<DataDic> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				List<Predicate> predicates = new ArrayList<>();
					
				if(CheckTool.isString(dataDicName)) {
					Path<String> path = root.get("dataDicName");
					Predicate predicate = cb.equal(path, dataDicName);
					predicates.add(predicate);
				}
				if(CheckTool.isString(dataDicValue)) {
					Path<String> path = root.get("dataDicValue");
					Predicate predicate = cb.like(path, "%"+dataDicValue+"%");
					predicates.add(predicate);
				}
				
				return cb.and(predicates.toArray(new Predicate[] {}));
			}
		};
		return dataDicRepository.findAll(specification, new PageRequest(index, size, Sort.Direction.ASC,"id"));
	}
}
