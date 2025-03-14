package com.wizard.task;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.wizard.service.TradingViewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

/**
 * @author wizard
 * @date 2024-06-03
 * @desc
 */
@Slf4j
@Configuration
@EnableScheduling
public class TradingViewTsk {

	@Resource
	TradingViewService tradingViewService;

	/**
	 * 调用tv数据，检测强势标的。从0时起，每1小时零2秒执行一次
	 */
	//@Scheduled(cron = "2 0 0/1 * * ?")
	public void scan(){
		Long logId = IdWorker.getId();
		log.info("日志ID:{},开始检测强势标的",logId);
		tradingViewService.scan(logId);
		log.info("日志ID:{},完成检测强势标的",logId);
	}
}
