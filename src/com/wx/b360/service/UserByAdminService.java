package com.wx.b360.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.wx.b360.entity.UserByAdmin;
import com.wx.b360.repository.UserByAdminRepository;
import com.wx.b360.tool.CheckTool;

@Service
public class UserByAdminService {
	
	@Resource UserByAdminRepository userByAdminRepository;
	
	public Page<UserByAdmin> find(int index, int size, String name, String company){
		Specification<UserByAdmin> specification = new Specification<UserByAdmin>() {

			@Override
			public Predicate toPredicate(Root<UserByAdmin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				if(CheckTool.isString(name)) {
					Expression<String> path = root.get("name");
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
		return userByAdminRepository.findAll(specification, new PageRequest(index, size, Sort.Direction.DESC,"id"));
	}
		
}
