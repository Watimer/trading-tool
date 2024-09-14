package com.wizard.task;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.binance.connector.futures.client.impl.UMWebsocketClientImpl;
import com.wizard.model.vo.KLine;
import com.wizard.service.FutureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


/**
 * @author 巫师
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
	 * 从零时开始,每五分钟执行一次
	 */
	@Async
	//@Scheduled(cron = "* 0/5 0/1 * * ? ")
	public void openInterestStatistics(){
		Long logId = IdWorker.getId();
		log.info("日志ID:{},开始检测合约持仓量是否存在异动",logId);
		futureService.openInterestStatistics(logId);
		log.info("日志ID:{},完成检测合约持仓量是否存在异动",logId);
	}

	/**
	 * 监控是否存在新增标的
	 * 每分钟执行一次
	 */
	@Async
	//@Scheduled(fixedRate = 60000)
	public void checkNewSymbol(){
		Long logId = IdWorker.getId();
		log.info("日志ID:{},开始检测是否存在新增标的",logId);
		futureService.checkNewSymbol(logId);
		log.info("日志ID:{},完成检测是否存在新增标的",logId);
	}

	/**
	 * 计算币安费率
	 * 从零时开始,每四小执行一次
	 * 并推送负费率数据
	 */
	@Async
	//@Scheduled(fixedRate = 4 * 60 * 60 * 1000)
	public void getRate(){
		Long logId = IdWorker.getId();
		log.info("日志ID:{},开始检测当前资金费率",logId);
		futureService.getRate(logId);
		log.info("日志ID:{},完成检测当前资金费率",logId);
	}

	//public static void main(String[] args) {
	//	UMWebsocketClientImpl client = new UMWebsocketClientImpl();
	//	List<String> lookList = Arrays.asList("BTCUSDT","ETHUSDT","SOLUSDT","BNBUSDT");
	//	client.allTickerStream(event->{
	//		List<KLine> kLines = JSONArray.parseArray(event.toString(),KLine.class);
	//		kLines.stream().forEach(item->{
	//			// 标的
	//			String symbol = item.getS();
	//			// 当前价格
	//			String currentPrice = item.getC();
	//			if(lookList.contains(symbol)){
	//				System.out.println(DateUtil.now()+", 标的:【"+symbol+"】, 当前价格:【"+currentPrice+"】");
	//			}
	//		});
	//	});
	//}
}
