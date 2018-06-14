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

import com.wx.b360.entity.Acceptance;
import com.wx.b360.repository.AcceptanceRepository;
import com.wx.b360.tool.CheckTool;

@Service
public class AcceptanceService {
	
	@Resource AcceptanceRepository acceptanceRepository;
	
	public Page<Acceptance> find(int index, int size, String invoice, String core, Integer is_finish){
		Specification<Acceptance> specification = new Specification<Acceptance>() {

			@Override
			public Predicate toPredicate(Root<Acceptance> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				if(invoice != null) {
					Expression<String> path = root.get("invoice");
					Predicate predicate = cb.like(path, "%"+invoice+"%");
					predicates.add(predicate);
				}
				if(CheckTool.isString(core)) {
					Path<String> path = root.get("core");
					Predicate predicate = cb.like(path, "%"+core+"%");
					predicates.add(predicate);
				}
				if(is_finish != null) {
					Path<Integer> path = root.get("is_finish");
					Predicate predicate = cb.equal(path, is_finish);
					predicates.add(predicate);
				}
				return cb.and(predicates.toArray(new Predicate[] {}));
			}
	
		};
		return acceptanceRepository.findAll(specification, new PageRequest(index, size, Sort.Direction.DESC,"id"));
	}
		
}
