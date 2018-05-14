package com.wx.b360.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wx.b360.repository.CardRepository;

@Service
public class CardService {
	@Resource CardRepository cardRepository;
}
