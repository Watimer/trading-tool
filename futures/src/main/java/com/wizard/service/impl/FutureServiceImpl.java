package com.wizard.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.net.NetUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.wizard.model.vo.InterestHistVO;
import com.wizard.service.FutureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

	/**
	 * 异步方法计算币安合约持仓量
	 */
	@Async
	@Override
	public void openInterestStatistics(Long logId) {
		log.info("日志ID:{},进入合约持仓量计算方法",logId);
		// TODO 以下方法需要抽取为公共方法,实现传入不同标的以及对应计算规则,动态计算是否符合通知条件
		LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

		String proxyUrl = "https://proxy-52x6ddbv1-watimers-projects.vercel.app/";
		String baseURL = "https/fapi.binance.com";

		UMFuturesClientImpl client = new UMFuturesClientImpl(proxyUrl+baseURL);

		parameters.put("symbol", "BTCUSDT");
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
					// TODO 此处需要重新写计算规则
					if(previousInterestHistVO.getSumOpenInterestValue().compareTo(interestHistVO.getSumOpenInterestValue()) > 0){
						log.info("日志ID:{},当前时间:{},价值减少",logId,DateTime.of(interestHistVO.getTimestamp()));
					} else {
						log.info("日志ID:{},当前时间:{},价值增加",logId,DateTime.of(interestHistVO.getTimestamp()));
						StringBuffer stringBuffer = new StringBuffer();
						stringBuffer.append("标的:").append(interestHistVO.getSymbol())
								.append("\n")
								.append("时间:").append(DateTime.of(interestHistVO.getTimestamp()))
								.append("\n")
								.append("平台:BINANCE")
								.append("\n")
								.append("持仓量:").append("增加")
								.append("\n")
								.append("方向:").append("Long");
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("msg_type","text");
						JSONObject jsonObjectContent = new JSONObject();
						jsonObjectContent.put("text",stringBuffer.toString());
						jsonObject.put("content",jsonObjectContent);
						log.info("日志ID:{},提醒信息:{}",logId,jsonObject);
						if(pushFlag){
							pushFlag = Boolean.FALSE;
							String URL = "https://open.feishu.cn/open-apis/bot/v2/hook/48e0a8c2-b69e-466f-a4ab-9928b8657568";
							// 调用飞书机器人接口
							String feiShuResult = HttpRequest.post(URL)
									.header("Content-Type", "application/json")
									.body(jsonObject.toString())
									.execute()
									.body();
							JSONObject jsonFeiShu = JSONObject.parseObject(feiShuResult);
							log.info("日志ID:{},本机IP:{},飞书机器人返回信息:{}",logId, NetUtil.localIpv4s().toString(),feiShuResult);
							if(0==jsonFeiShu.getInteger("StatusCode")){
								log.info("日志ID:{},消息发送成功",logId);
							} else {
								log.error("日志ID:{},消息发送失败",logId);
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
