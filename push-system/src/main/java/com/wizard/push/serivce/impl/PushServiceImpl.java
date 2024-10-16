package com.wizard.push.serivce.impl;

import cn.hutool.core.net.NetUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.wizard.common.enums.ExchangeEnum;
import com.wizard.common.enums.PushEnum;
import com.wizard.push.serivce.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author 巫师
 * @date 2024-05-08
 * @desc
 */
@Slf4j
@Service
public class PushServiceImpl implements PushService {

	@Value("${FEISHU.FS_WEBHOOK_URL}")
	private String FS_WEBHOOK_URL;

	@Override
	public Boolean pushFeiShu(Long logId, String symbol, String dateTime, String title, ExchangeEnum exchangeEnum, PushEnum pushEnum) {
		Boolean resultFlag = Boolean.FALSE;

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("警报类型:").append(pushEnum.getDescription().replace(":",""))
				.append("\n")
				.append("\n")
				.append("标的:").append(symbol)
				.append("\n")
				.append("平台:").append(exchangeEnum.getEnName())
				.append("\n")
				.append(pushEnum.getDescription()).append(pushEnum.getDetail())
				.append("\n")
				.append("方向:").append(pushEnum.getDirection())
				.append("\n")
				.append("时间:").append(dateTime);
		com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
		jsonObject.put("msg_type","text");
		com.alibaba.fastjson.JSONObject jsonObjectContent = new com.alibaba.fastjson.JSONObject();
		jsonObjectContent.put("text",stringBuffer.toString());
		jsonObject.put("content",jsonObjectContent);
		log.info("日志ID:{},推送地址:{},提醒信息:{}",logId,FS_WEBHOOK_URL,jsonObject);

		// 调用飞书机器人接口
		//String feiShuResult = HttpRequest.post(FS_WEBHOOK_URL)
		//		.header("Content-Type", "application/json")
		//		.body(jsonObject.toString())
		//		.execute()
		//		.body();
		//JSONObject jsonFeiShu = JSONObject.parseObject(feiShuResult);
		//log.info("日志ID:{},本机IP:{},飞书机器人返回信息:{}",logId, NetUtil.localIpv4s().toString(),feiShuResult);
		//if(0==jsonFeiShu.getInteger("StatusCode")){
		//	log.info("日志ID:{},消息发送成功",logId);
		//	resultFlag = Boolean.TRUE;
		//} else {
		//	log.error("日志ID:{},消息发送失败",logId);
		//}
		return pushMessage(logId,jsonObject);
	}

	@Override
	public Boolean pushManySymbol(Long logId, String context) {
		com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
		jsonObject.put("msg_type","text");
		com.alibaba.fastjson.JSONObject jsonObjectContent = new com.alibaba.fastjson.JSONObject();
		jsonObjectContent.put("text",context);
		jsonObject.put("content",jsonObjectContent);
		return pushMessage(logId,jsonObject);
	}

	@Override
	public Boolean pushImage(Long logId, String imageKey) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("msg_type","image");
		JSONObject jsonContent = new JSONObject();
		jsonContent.put("image_key",imageKey);
		jsonObject.put("content",jsonContent);
		return pushMessage(logId,jsonObject);
	}

	public Boolean pushMessage(Long logId,JSONObject jsonObject){
		Boolean resultFlag = Boolean.FALSE;
		List<String> pushUrlList = Arrays.asList(FS_WEBHOOK_URL.split(","));
		for (String pushUrl : pushUrlList) {
			// 调用飞书机器人接口
			String feiShuResult = HttpRequest.post(pushUrl)
					.header("Content-Type", "application/json")
					.body(jsonObject.toString())
					.execute()
					.body();
			JSONObject jsonFeiShu = JSONObject.parseObject(feiShuResult);
			log.info("日志ID:{},本机IP:{},飞书机器人返回信息:{}",logId, NetUtil.localIpv4s().toString(),feiShuResult);
			if(0==jsonFeiShu.getInteger("StatusCode")){
				log.info("日志ID:{},消息发送成功",logId);
				resultFlag = Boolean.TRUE;
			} else {
				log.error("日志ID:{},消息发送失败",logId);
				resultFlag = Boolean.FALSE;
			}
		}
		return resultFlag;
	}
}
