package com.wx.b360.service;

import java.math.BigDecimal;
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

import com.sun.xml.internal.ws.developer.StreamingAttachment;
import com.wx.b360.entity.Bill;
import com.wx.b360.repository.BillRepository;
import com.wx.b360.tool.CheckTool;

@Service
public class BillService {
	@Resource BillRepository billRepository;

	public Page<Bill> find(int index, int size, String staffName, Integer status, Integer type, String core, String invoice) {
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
					Path<Integer> path = root.get("acceptance").get("type");
					Predicate predicate = cb.equal(path, type);
					predicates.add(predicate);
				}
				if(CheckTool.isString(staffName)) {
					Path<String> path = root.get("staff").get("name");
					Predicate predicate = cb.like(path, "%"+staffName+"%");
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
	
	public BigDecimal calculationInterest(Bill bill) {
		BigDecimal aYInterest = null;
		int offer = bill.getOffer();
		if(offer == 0) {
			BigDecimal rate = bill.getRate();
			aYInterest = rate.multiply(new BigDecimal(100000)).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP);
		}else if(offer == 1) {
			aYInterest = bill.getDeductions();
		}else if(offer == 2) {
			BigDecimal direct = bill.getDirect();
			aYInterest = direct.multiply(new BigDecimal(100000)).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP);
		}else if(offer == 3) {
			BigDecimal rate = bill.getRate();
			BigDecimal deductions = bill.getDeductions();
			aYInterest = deductions.add(rate.multiply(new BigDecimal(100000)).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
		}
		
		return aYInterest;
	}
}
