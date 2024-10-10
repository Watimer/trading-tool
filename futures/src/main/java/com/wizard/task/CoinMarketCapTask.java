package com.wizard.task;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.wizard.model.po.SymbolInfoPo;
import com.wizard.model.po.VolumeInfoPo;
import com.wizard.service.SymbolInfoService;
import com.wizard.service.VolumeInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author wizard
 * @date 2024-09-18
 * @desc
 */
@Slf4j
@Configuration
@EnableScheduling
public class CoinMarketCapTask {

	@Resource
	VolumeInfoService volumeInfoService;

	@Resource
	SymbolInfoService symbolInfoService;

	//@Scheduled(fixedRate = 60000)
	public void queryVolumeTask(){
		Long logId = IdWorker.getId();
		List<String> symbolList = new ArrayList<>();
		List<SymbolInfoPo> symbolInfoPoList = symbolInfoService.listSymbolInfoPo(logId,new LambdaQueryWrapper<>(SymbolInfoPo.class).eq(SymbolInfoPo::getDelFlag,0));
		symbolInfoPoList.stream().forEach(item ->{
			symbolList.add(item.getSymbolName().toLowerCase(Locale.ROOT));
		});
		Random read = new Random();
		for (String symbol : symbolList){
			int i = read.nextInt(1);
			log.info("日志ID:{},标的:{},睡眠时间:{}",logId,symbol,i);
			ThreadUtil.sleep(i, TimeUnit.SECONDS);
			log.info("日志ID:{},标的:{},开始执行",logId,symbol);
			try {
				getSymbolVolume(logId,symbol);
				log.info("日志ID:{},标的:{},执行完成。",logId,symbol);
			} catch (Exception e) {
				log.error("日志ID:{},标的:{},执行失败。",logId,symbol);
				log.error("{}",e);
			}

		}
	}


	public void getSymbolVolume(Long logId,String symbol) {

		log.info("日志ID:{},查询交易量-标的:{},开始。",logId,symbol);
		String API_URL = "https://api.coinmarketcap.com/data-api/v3/cryptocurrency/market-pairs/latest?slug="+symbol+"&start=1&limit=10&category=spot&centerType=all&sort=cmc_rank_advanced&direction=desc&spotUntracked=true";
		//String API_URL = "https://api.coinmarketcap.com/data-api/v3/cryptocurrency/market-pairs/latest?slug="+symbol+"&start=1&quoteCurrencyId=825&limit=10&category=spot&centerType=all&sort=cmc_rank_advanced&direction=desc&spotUntracked=true";

		String result = HttpRequest
				.get(API_URL)
				//.setHttpProxy("127.0.0.1",7897)
				.execute().body();


		if(StrUtil.isBlank(result)){
			return;
		}
		JSONObject jsonObject = JSONObject.parseObject(result);
		if(ObjectUtil.isNull(jsonObject)){
			return;
		}
		JSONObject statusJson = jsonObject.getJSONObject("status");
		if(ObjectUtil.isNull(statusJson)){
			return;
		}
		if(!"0".equals(statusJson.getString("error_code"))){
			// TODO 接口请求失败
			return;
		}
		JSONObject jsonData = jsonObject.getJSONObject("data");
		if(ObjectUtil.isNull(jsonData)){
			return;
		}

		List<JSONObject> jsonObjectList = JSONArray.parseArray(jsonData.getString("marketPairs"), JSONObject.class);
		Long groupFlag = IdWorker.getId();
		List<VolumeInfoPo> volumeInfoPoList = new ArrayList<>();
		for (JSONObject marketPairs : jsonObjectList) {
			VolumeInfoPo volumeInfoPo = new VolumeInfoPo();
			volumeInfoPo.setId(IdWorker.getId());
			volumeInfoPo.setGroupFlag(groupFlag);
			volumeInfoPo.setSymbol(marketPairs.getString("baseSymbol"));
			volumeInfoPo.setCurrentPrice(marketPairs.getBigDecimal("price").setScale(2, BigDecimal.ROUND_CEILING));
			volumeInfoPo.setExchangeName(marketPairs.getString("exchangeName"));
			volumeInfoPo.setExchangeVolume(marketPairs.getBigDecimal("volumeUsd").setScale(2, BigDecimal.ROUND_CEILING));
			volumeInfoPo.setEffectiveLiquidity(marketPairs.getInteger("effectiveLiquidity"));
			volumeInfoPo.setExchangeProportion(marketPairs.getBigDecimal("volumePercent").setScale(2, BigDecimal.ROUND_CEILING));
			volumeInfoPo.setMarketReputation(marketPairs.getInteger(("marketReputation")));
			volumeInfoPo.setQuoteSymbol(marketPairs.getString("quoteSymbol"));
			volumeInfoPo.setCreateTime(DateTime.now());
			volumeInfoPo.setDelFlag(0);
			volumeInfoPo.setLevel("24h");
			volumeInfoPoList.add(volumeInfoPo);
		}
		log.info("日志ID:{},查询交易量-标的:{},完成:{}",logId,symbol, JSONObject.toJSONString(volumeInfoPoList));
		volumeInfoService.saveBatch(logId, volumeInfoPoList);
	}
}
