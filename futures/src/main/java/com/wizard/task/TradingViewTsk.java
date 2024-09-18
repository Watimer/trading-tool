package com.wizard.task;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.wizard.service.TradingViewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

/**
 * @author 岳耀栎
 * @date 2024-06-03
 * @desc
 */
@Slf4j
@Configuration
@EnableScheduling
public class TradingViewTsk {

	@Resource
	TradingViewService tradingViewService;

	//@Scheduled(fixedRate = 60000)
	public void scan(){
		Long logId = IdWorker.getId();
		log.info("日志ID:{},开始检测强势标的",logId);
		tradingViewService.scan(logId);
		log.info("日志ID:{},完成检测强势标的",logId);
	}
}
