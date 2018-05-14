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

import com.wx.b360.entity.Source;
import com.wx.b360.repository.SourceRepository;
import com.wx.b360.tool.CheckTool;

@Service
public class SourceService {
	@Resource SourceRepository sourceRepository;

	public Page<Source> find(int index, int size, Integer level, String invoice) {
		// TODO Auto-generated method stub
		Specification<Source> specification = new Specification<Source>() {
			@Override
			public Predicate toPredicate(Root<Source> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				List<Predicate> predicates = new ArrayList<>();
				if(level != null) {
					Path<Integer> path = root.get("level");
					Predicate predicate = cb.equal(path, level);
					predicates.add(predicate);
				}
				if(CheckTool.isString(invoice)) {
					Path<String> path = root.get("invoice");
					Predicate predicate = cb.like(path, "%"+invoice+"%");
					predicates.add(predicate);
				}
				return cb.and(predicates.toArray(new Predicate[] {}));
			}
		};
		return sourceRepository.findAll(specification, new PageRequest(index, size, Sort.Direction.DESC, "level","id"));
	}
}
