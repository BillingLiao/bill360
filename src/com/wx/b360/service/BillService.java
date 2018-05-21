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

import com.wx.b360.entity.Bill;
import com.wx.b360.repository.BillRepository;
import com.wx.b360.tool.CheckTool;

@Service
public class BillService {
	@Resource BillRepository billRepository;

	public Page<Bill> find(int index, int size, Integer status, Integer type, String core, String invoice) {
		Specification<Bill> specification = new Specification<Bill>() {

			@Override
			public Predicate toPredicate(Root<Bill> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				if(status != null) {
					Path<Integer> path = root.get("status");
					Predicate predicate = cb.equal(path, status);
					predicates.add(predicate);
				}
				if(type != null) {
					Path<Integer> path = root.get("type");
					Predicate predicate = cb.equal(path, type);
					predicates.add(predicate);
				}
				if(CheckTool.isString(core)) {
					Path<String> path = root.get("acceptance").get("core");
					Predicate predicate = cb.like(path, "%"+core+"%");
					predicates.add(predicate);
				}
				if(CheckTool.isString(invoice)) {
					Path<String> path = root.get("acceptance").get("invoice");
					Predicate predicate = cb.like(path, "%"+invoice+"%");
					predicates.add(predicate);
				}
				return cb.and(predicates.toArray(new Predicate[] {}));
			}
	
		};
		return billRepository.findAll(specification, new PageRequest(index, size, Sort.Direction.DESC, "level","id"));
	}
}
