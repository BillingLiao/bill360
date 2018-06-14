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

import com.wx.b360.entity.Staff;
import com.wx.b360.repository.StaffRepository;
import com.wx.b360.tool.CheckTool;

@Service
public class StaffService {
	
	@Resource StaffRepository staffRepository;
	
	public Page<Staff> find(int index, int size, String name, String company){
		Specification<Staff> specification = new Specification<Staff>() {

			@Override
			public Predicate toPredicate(Root<Staff> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				if(CheckTool.isString(name)) {
					Path<String> path = root.get("name");
					Predicate predicate = cb.like(path, "%"+name+"%");
					predicates.add(predicate);
				}
				if(CheckTool.isString(company)) {
					Path<String> path = root.get("company");
					Predicate predicate = cb.like(path, "%"+company+"%");
					predicates.add(predicate);
				}
				
				return cb.and(predicates.toArray(new Predicate[] {}));
			}
	
		};
		return staffRepository.findAll(specification, new PageRequest(index, size, Sort.Direction.DESC,"id"));
	}
		
}
