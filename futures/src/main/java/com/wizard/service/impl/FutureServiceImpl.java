package com.wizard.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.wizard.common.enums.ExchangeEnum;
import com.wizard.common.enums.PushEnum;
import com.wizard.component.CheckComponent;
import com.wizard.component.GlobalListComponent;
import com.wizard.model.vo.InterestHistVO;
import com.wizard.model.vo.SymbolFundingRateVO;
import com.wizard.push.serivce.PushService;
import com.wizard.service.FutureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author 巫师
 * @date 2024-05-07
 * @desc
 */
@Slf4j
@Service
public class FutureServiceImpl implements FutureService {

	@Resource
	PushService pushService;

	@Resource
	GlobalListComponent globalListComponent;

	@Value("${PROXY.URL}")
	private String PROXY_URL;

	/**
	 * 检测是否存在新增标的
	 *
	 * @param logId 日志ID
	 */
	@Override
	public void checkNewSymbol(Long logId) {
		// 获取全部交易标的
		List<String> symbolList = getExchangeInfo(logId);
		List<String> alreadyList = globalListComponent.getGlobalList();
		boolean isEqual = symbolList.stream().allMatch(alreadyList::contains);
		if(!isEqual){
			// 最新可交易列表中存在的标的
			List<String> newSymbolList = symbolList.stream()
					.filter(element -> !alreadyList.contains(element))
					.collect(Collectors.toList());

			// 最新可交易列表中存在的标的
			List<String> deleteSymbolList = alreadyList.stream()
					.filter(element -> !symbolList.contains(element))
					.collect(Collectors.toList());
			// 存在新增标的
			if(!newSymbolList.isEmpty()){
				for (String symbol : newSymbolList) {
					Boolean pushFlag = pushService.pushFeiShu(logId,symbol, DateUtil.now(),"", ExchangeEnum.EXCHANGE_BINANCE, PushEnum.FUTURES_SYMBOL_ADD);
					if(pushFlag){
						log.info("日志ID:{},标的:{},推送消息成功",logId,symbol);
					}
				}
			}
			// 存在去除的标的
			if(!deleteSymbolList.isEmpty()){
				for (String symbol : newSymbolList) {
					Boolean pushFlag = pushService.pushFeiShu(logId,symbol, DateUtil.now(),"", ExchangeEnum.EXCHANGE_BINANCE, PushEnum.FUTURES_SYMBOL_DELETE);
					if(pushFlag){
						log.info("日志ID:{},标的:{},推送消息成功",logId,symbol);
					}
				}
			}
			// 清空列表
			globalListComponent.removeAll(logId);

			// 重新添加数据
			globalListComponent.addToGlobalList(logId,symbolList);
		}
	}

	@Resource
	CheckComponent checkComponent;

	/**
	 * 异步方法计算币安合约持仓量
	 */
	@Async
	@Override
	public void openInterestStatistics(Long logId) {
		log.info("日志ID:{},进入合约持仓量计算方法",logId);
		// TODO 以下方法需要抽取为公共方法,实现传入不同标的以及对应计算规则,动态计算是否符合通知条件
		// 获取全部交易标的
		List<String> symbolList = globalListComponent.getGlobalList();
		if(symbolList.isEmpty()){
			return;
		}
		ExecutorService executor = Executors.newFixedThreadPool(2);
		CompletableFuture<Void> allOf = null;
		for (String symbol : symbolList) {
			//checkComponent.checkInterestStatistics(logId,symbol);
			CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
							checkComponent.checkInterestStatistics(logId,symbol)
					,executor);
			allOf = CompletableFuture.allOf(future);
		}

		allOf.thenRun(executor::shutdown);
		// 等待所有线程执行完成
		allOf.join();
		//executor.shutdown();
	}

	@Override
	public List<String> getExchangeInfo(Long logId) {
		List<String> resultList = new ArrayList<>();
		String proxyUrl = "https://trade1818.top/";
		String baseURL = "https/fapi.binance.com";
		UMFuturesClientImpl client = new UMFuturesClientImpl(PROXY_URL);

		try {
			String result = client.market().exchangeInfo();
			JSONObject jsonObject = JSONObject.parseObject(result);
			JSONArray jsonArray = jsonObject.getJSONArray("symbols");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject1 = jsonArray.getJSONObject(i);
				resultList.add(jsonObject1.getString("symbol"));
			}
			log.info("日志ID:{},当前标的数量:{}",logId,resultList.size());
		} catch (BinanceConnectorException e) {
			log.error("fullErrMessage: {}", e.getMessage(), e);
		} catch (BinanceClientException e) {
			log.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
					e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
		}

		return resultList;
	}

	/**
	 * 检测资金费率
	 *
	 * @param logId
	 */
	@Override
	public void getRate(Long logId) {
		LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
		String proxyUrl = "https://trade1818.top/";
		String baseURL = "https/fapi.binance.com";
		UMFuturesClientImpl client = new UMFuturesClientImpl(PROXY_URL);

		try {
			String result = client.market().fundingRate(parameters);
			List<SymbolFundingRateVO> symbolFundingRateVOList = JSONArray.parseArray(result,SymbolFundingRateVO.class);
			// 过滤出负费率标的
			BigDecimal bigDecimal = new BigDecimal("0");
			List<SymbolFundingRateVO> tempList = symbolFundingRateVOList.stream()
					.filter(item -> item.getFundingRate().compareTo(bigDecimal)<0)
					.sorted(Comparator.comparing(SymbolFundingRateVO::getFundingRate))
					.collect(Collectors.toList());
			if(!tempList.isEmpty()){
				for (SymbolFundingRateVO symbolFundingRateVO:tempList) {
					PushEnum pushEnum = PushEnum.FUTURES_SYMBOL_RATE;
					BigDecimal rateResult = symbolFundingRateVO.getFundingRate().multiply(new BigDecimal("100"));
					pushEnum.setDescription("资金费率:", String.format("%.4f%%",rateResult));
					Boolean pushFlag = pushService.pushFeiShu(logId,symbolFundingRateVO.getSymbol(),
							DateTime.of(symbolFundingRateVO.getFundingTime()).toString(),"", ExchangeEnum.EXCHANGE_BINANCE, PushEnum.FUTURES_SYMBOL_RATE);
					if(pushFlag){
						log.info("日志ID:{},标的:{},推送消息成功",logId,symbolFundingRateVO.getSymbol());
					}
				}
			}
			log.info("{}",result);
		} catch (BinanceConnectorException e) {
			log.error("fullErrMessage: {}", e.getMessage(), e);
		} catch (BinanceClientException e) {
			log.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
					e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
		}
	}
}
