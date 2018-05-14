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

import com.wx.b360.entity.Inventory;
import com.wx.b360.entity.User;
import com.wx.b360.entity.UserByAdmin;
import com.wx.b360.repository.InventoryRepository;
import com.wx.b360.tool.CheckTool;

@Service
public class InventoryService {
	@Resource InventoryRepository inventoryRepository;

	public Page<Inventory> find(int index, int size, User user, UserByAdmin userByAdmin, String company) {
		// TODO Auto-generated method stub
		Specification<Inventory> specification = new Specification<Inventory>() {
			
			@Override
			public Predicate toPredicate(Root<Inventory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				List<Predicate> predicates = new ArrayList<>();
				
				if(user != null) {
					Path<User> path = root.get("user");
					Predicate predicate = cb.equal(path, user);
					predicates.add(predicate);
				}
				
				if(userByAdmin != null) {
					Path<UserByAdmin> path = root.get("userByAdmin");
					Predicate predicate = cb.equal(path, userByAdmin);
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
		return inventoryRepository.findAll(specification, new PageRequest(index, size, Sort.Direction.ASC,"id"));
	}
}
