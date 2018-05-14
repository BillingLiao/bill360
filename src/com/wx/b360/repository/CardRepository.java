package com.wx.b360.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.wx.b360.entity.Card;
import com.wx.b360.entity.User;

public interface CardRepository extends PagingAndSortingRepository<Card, Integer>, JpaSpecificationExecutor<Card> {

	List<Card> findByUser(User user);

}
