package com.wx.b360.config;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling // 启用定时任务
public class SchedulingConfig {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    //定时任务
//    @Scheduled(cron = "0 0 1 1 * ?") // 每月的1号凌晨1点执行
//    @Scheduled(cron = "0/1 * * * * ?") // 每隔1秒
    public void scheduler() {
//    	logger.info("定时任务");
    }

}