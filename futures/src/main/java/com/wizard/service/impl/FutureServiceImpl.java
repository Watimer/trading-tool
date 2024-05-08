package com.wizard.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.net.NetUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.wizard.common.enums.ExchangeEnum;
import com.wizard.common.enums.PushEnum;
import com.wizard.model.vo.InterestHistVO;
import com.wizard.push.serivce.PushService;
import com.wizard.service.FutureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author 岳耀栎
 * @date 2024-05-07
 * @desc
 */
@Slf4j
@Service
public class FutureServiceImpl implements FutureService {

	@Resource
	PushService pushService;

	/**
	 * 异步方法计算币安合约持仓量
	 */
	@Async
	@Override
	public void openInterestStatistics(Long logId) {
		log.info("日志ID:{},进入合约持仓量计算方法",logId);
		// TODO 以下方法需要抽取为公共方法,实现传入不同标的以及对应计算规则,动态计算是否符合通知条件
		// 获取全部交易标的
		List<String> symbolList = getExchangeInfo();
		for (String symbol : symbolList) {
			LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

			String proxyUrl = "https://proxy-52x6ddbv1-watimers-projects.vercel.app/";
			String baseURL = "https/fapi.binance.com";

			UMFuturesClientImpl client = new UMFuturesClientImpl();

			parameters.put("symbol", symbol);
			parameters.put("period", "5m");
			parameters.put("limit",2);
			Boolean pushFlag = Boolean.TRUE;
			try {
				String result = client.market().openInterestStatistics(parameters);
				// 转化结果
				List<InterestHistVO> interestHistVOList = JSONArray.parseArray(result,InterestHistVO.class);
				InterestHistVO previousInterestHistVO = null;
				// 根据时间顺序，计算当前持仓量是否大于上一个交易单位的持仓量
				for (int i = interestHistVOList.size(); i > 0; i--) {
					if(i!=interestHistVOList.size()){
						previousInterestHistVO = interestHistVOList.get(i);
					}
					InterestHistVO interestHistVO = interestHistVOList.get(i - 1);
					log.info("日志ID:{},当前时间:{},持仓量:{},持仓价值:{}",logId, DateTime.of(interestHistVO.getTimestamp()),interestHistVO.getSumOpenInterest(),interestHistVO.getSumOpenInterestValue());
					if(null != previousInterestHistVO){
						// 此处需要重新写计算规则
						synchronized (this) {
							BigDecimal compareResult = previousInterestHistVO.getSumOpenInterest().divide(interestHistVO.getSumOpenInterest(),2);
							if(compareResult.compareTo(new BigDecimal("1.5")) < 0){
								log.info("日志ID:{},当前时间:{},价值减少",logId,DateTime.of(interestHistVO.getTimestamp()));
							} else {
								log.info("日志ID:{},当前时间:{},价值增加",logId,DateTime.of(interestHistVO.getTimestamp()));
								if(pushFlag){
									pushFlag = Boolean.FALSE;
									pushService.pushFeiShu(logId,interestHistVO.getSymbol(),
											DateTime.of(interestHistVO.getTimestamp()).toString(),"", ExchangeEnum.EXCHANGE_BINANCE, PushEnum.FUTURES_OPEN_INTEREST_LONG);

								}
							}
						}

					}
				}
			} catch (BinanceConnectorException e) {
				log.error("fullErrMessage: {}", e.getMessage(), e);
			} catch (BinanceClientException e) {
				log.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
						e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
			}
		}

	}

	public List<String> getExchangeInfo() {
		List<String> resultList = new ArrayList<>();
		UMFuturesClientImpl client = new UMFuturesClientImpl();

		try {
			String result = client.market().exchangeInfo();
			JSONObject jsonObject = JSONObject.parseObject(result);
			JSONArray jsonArray = jsonObject.getJSONArray("symbols");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject1 = jsonArray.getJSONObject(i);
				resultList.add(jsonObject1.getString("symbol"));
			}
			log.info(result);
		} catch (BinanceConnectorException e) {
			log.error("fullErrMessage: {}", e.getMessage(), e);
		} catch (BinanceClientException e) {
			log.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
					e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
		}

		return resultList;
	}
}
