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
import com.wx.b360.entity.Order;
import com.wx.b360.entity.User;
import com.wx.b360.repository.OrderRepository;
import com.wx.b360.tool.CheckTool;

@Service
public class OrderService {
	@Resource OrderRepository orderRepository;

	public Page<Order> find(int index, int size, Integer status, User user, String invoice, Integer id) {
		Specification<Order> specification = new Specification<Order>() {

			@Override
			public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				if(status != null) {
					Path<Integer> path = root.get("status");
					Predicate predicate = cb.equal(path, status);
					predicates.add(predicate);
				}
				if(user != null) {
					Path<User> path = root.get("user");
					Predicate predicate = cb.equal(path, user);
					predicates.add(predicate);
				}
				if(CheckTool.isString(invoice)) {
					Path<String> path = root.get("bill").get("acceptance").get("invoice");
					Predicate predicate = cb.like(path, "%" + invoice + "%");
					predicates.add(predicate);
				}
				if(id != null) {
					Path<Integer> path = root.get("id");
					Predicate predicate = cb.equal(path, id);
					predicates.add(predicate);
				}
				return cb.and(predicates.toArray(new Predicate[] {}));
			}
	
		};
		return orderRepository.findAll(specification, new PageRequest(index, size, Sort.Direction.DESC, "id"));
	}
}
