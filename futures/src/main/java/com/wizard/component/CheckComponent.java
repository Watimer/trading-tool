package com.wizard.component;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONArray;
import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.wizard.common.enums.ExchangeEnum;
import com.wizard.common.enums.PushEnum;
import com.wizard.model.vo.InterestHistVO;
import com.wizard.push.serivce.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author 岳耀栎
 * @date 2024-05-08
 * @desc
 */
@Slf4j
@Component
public class CheckComponent {

	@Resource
	PushService pushService;

	private String ADD_NUMBER = "1.2";

	@Async
	public void checkInterestStatistics(Long logId,String symbol){
		LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

		String proxyUrl = "https://proxy-52x6ddbv1-watimers-projects.vercel.app/";
		String baseURL = "https/fapi.binance.com";

		UMFuturesClientImpl client = new UMFuturesClientImpl();

		parameters.put("symbol", symbol);
		parameters.put("period", "5m");
		parameters.put("limit",2);
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
				log.info("日志ID:{},当前时间:{},标的:{},持仓量:{},持仓价值:{}",logId, interestHistVO.getSymbol(), DateTime.of(interestHistVO.getTimestamp()),interestHistVO.getSumOpenInterest(),interestHistVO.getSumOpenInterestValue());
				if(null != previousInterestHistVO){
					// 此处需要重新写计算规则
					//synchronized (this) {
					BigDecimal compareResult = interestHistVO.getSumOpenInterest().divide(previousInterestHistVO.getSumOpenInterest(),2,BigDecimal.ROUND_HALF_UP);
					log.info("日志ID:{},当前时间:{},标的:{},持仓量:{},计算结果:{}",logId,interestHistVO.getSymbol(), DateTime.of(interestHistVO.getTimestamp()),compareResult);
					if(compareResult.compareTo(new BigDecimal(ADD_NUMBER)) >= 0){
						log.info("日志ID:{},当前时间:{},标的:{},价值增加",logId,interestHistVO.getSymbol(),DateTime.of(interestHistVO.getTimestamp()));
						Boolean pushFlag = pushService.pushFeiShu(logId,interestHistVO.getSymbol(),
								DateTime.of(interestHistVO.getTimestamp()).toString(),"", ExchangeEnum.EXCHANGE_BINANCE, PushEnum.FUTURES_OPEN_INTEREST_LONG);
						if(pushFlag){
							log.info("日志ID:{},标的:{},推送消息成功",logId,interestHistVO.getSymbol());
						}
					}
					//}

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
