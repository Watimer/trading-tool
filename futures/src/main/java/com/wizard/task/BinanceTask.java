package com.wizard.task;

import com.wizard.service.FutureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;


/**
 * @author 岳耀栎
 * @date 2024-05-07
 * @desc
 */
@Slf4j
@Configuration
@EnableScheduling
public class BinanceTask {

	@Resource
	FutureService futureService;

	/**
	 * 计算币安合约持仓量
	 */
	@Scheduled(fixedDelay = 2*10*1000)
	public void openInterestStatistics(){
		Long logId = System.currentTimeMillis();
		futureService.openInterestStatistics(logId);
	}
}
