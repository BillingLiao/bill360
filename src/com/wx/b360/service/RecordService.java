package com.wx.b360.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.wx.b360.entity.Record;
import com.wx.b360.entity.User;
import com.wx.b360.model.Rank;
import com.wx.b360.repository.RecordRepository;
import com.wx.b360.tool.CheckTool;

@Service
public class RecordService {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource RecordRepository recordRepository;

	public Page<Record> find(int index, int size, User user, String company) {
		Specification<Record> specification = new Specification<Record>() {
			@Override
			public Predicate toPredicate(Root<Record> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				if(user != null) {
					Path<User> path = root.get("user");
					Predicate predicate = cb.equal(path, user);
					predicates.add(predicate);
				}
				if(company != null) {
					Path<String> path = root.get("word");
					Predicate predicate = cb.like(path, company);
					predicates.add(predicate);
				}
				return cb.and(predicates.toArray(new Predicate[] {}));
			}
		};
		return recordRepository.findAll(specification, new PageRequest(index, size, Sort.Direction.DESC, "time"));
	}
	
	public List<Rank> rank() {
		List<Rank> ranks = new ArrayList<>();
		try {
			List<Object> list = recordRepository.rank();
			for(Object obj : list) {
				String temp = JSON.toJSONString(obj);
				temp = temp.substring(1, temp.length()-1);
				if(CheckTool.isString(temp)) {
					if(CheckTool.isString(temp.split(",")[0]) && CheckTool.isInt(temp.split(",")[1])) {
						Rank rank = new Rank(temp.split(",")[0].replaceAll("\"", ""), Integer.parseInt(temp.split(",")[1]));
						ranks.add(rank);
					}
				}
			}
		} catch(Exception e) {
			logger.info("获取热词排行榜错误:" + e.getMessage());
		}
		return ranks;
	}


}
