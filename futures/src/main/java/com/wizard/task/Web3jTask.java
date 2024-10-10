package com.wizard.task;

import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.web3j.protocol.*;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * @author wizard
 * @date 2024-09-13
 * @desc
 */
@Slf4j
@Configuration
@EnableScheduling
public class Web3jTask {

	//public static void main(String[] args) throws IOException {
	//	extracted();
	//}

	private static void extracted() throws IOException {
		Web3j web3j = null;
		try {
			Web3jService web3jService = new HttpService("https://mainnet.infura.io/v3/370a89b064e84f999db395e561dc60b6");
			web3j = Web3j.build(web3jService);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		String tokenContractAddress = "0x532f520F060716a373a53B638B10dd0d552f6011";
		Request<?, EthGetBalance> ethGetBalanceRequest = web3j.ethGetBalance("0x532f520F060716a373a53B638B10dd0d552f6011",
				DefaultBlockParameter.valueOf(BigInteger.ONE));
		BigInteger integer =  ethGetBalanceRequest.send().getBalance();
		System.out.println(integer);

		EthFilter filter1 = new EthFilter(DefaultBlockParameterName.EARLIEST,DefaultBlockParameterName.LATEST,tokenContractAddress);
		List<EthLog.LogResult> logs = null;
		try {
			Request<?, EthLog> ethLogRequest = web3j.ethGetLogs(filter1);
			EthLog send = ethLogRequest.send();
			logs = send.getLogs();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		for (EthLog.LogResult item : logs) {
			log.info("{}", JSONObject.toJSONString(item));
		}
	}
}
