package com.wizard.component;

import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.binance.connector.futures.client.impl.UMWebsocketClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.LinkedHashMap;

/**
 * @author 岳耀栎
 * @date 2024-05-07
 * @desc
 */
@Slf4j
@Component
public class StartComponent {


	// 启动时执行
	//@PostConstruct
	public void init(){
		log.info("启动时执行1");
		UMWebsocketClientImpl websocketClient = new UMWebsocketClientImpl();
		websocketClient.klineStream("btcusdt", "1h", ((event) -> {
			//System.out.println(event);
			log.info("返回信息:{}", JSONObject.valueToString(event));
			//websocketClient.closeAllConnections();
		}));
	}

	//@PostConstruct
	public void init2(){
		log.info("启动时执行2");

	}
}
