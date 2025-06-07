package com.wizard.ads;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 岳耀栎
 * @date 2025-03-15
 * @desc
 */
@Slf4j
public class GrassTest {
	static String userName = "watimer@163.com";
	static String password = "qwer1234";
	static String API_BASE_URL = "https://api.getgrass.io";
	static String clientIp = "https://api.bigdatacloud.net/data/client-ip";
	static String checkIn = "https://director.getgrass.io/checkin";


	static String accessToken = "";
	static String refreshToken = "";
	static String userId = "";
	public static void main(String[] args) {
		login();
		clientIp();
		checkIn();
	}

	public static void login(){

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("username",userName);
		jsonObject.put("password",password);

	    String result =	HttpRequest.post(API_BASE_URL+"/login").body(JSONObject.toJSONString(jsonObject)).execute().body();

		JSONObject jsonResult = JSONObject.parseObject(result);
		log.info("登录结果:{}",jsonResult);
		JSONObject jsonData = jsonResult.getJSONObject("result").getJSONObject("data");
		accessToken =  jsonData.getString("accessToken");
		refreshToken =  jsonData.getString("refreshToken");
		userId =  jsonData.getString("userId");
	}

	public static void clientIp(){
		String body = HttpRequest.get(clientIp).execute().body();
		log.info("获取客户端IP:{}",body);
	}

	public static void checkIn(){
		String params = "{\"browserId\":\"6741ba30-097e-53f2-a9b5-165aaf5874a8\",\"userId\":\"2uMAhOFTXLCZSRJGn9r9BEWWhh0\",\"version\":\"5.1.1\",\"extensionId\":\"ilehaonighjijnmpnagapkhpcdbhclfg\",\"userAgent\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36\",\"deviceType\":\"extension\"}";


		Map<String,String> headers = new HashMap<>();
		headers.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36");
		headers.put("Cookie","_ga=GA1.1.1281586409.1742048096; _clck=28u955%7C2%7Cfu8%7C0%7C1900; _conv_r=s%3Aapp.getgrass.io*m%3Areferral*t%3A*c%3A; _gcl_au=1.1.1199629073.1742048131; _ga_7RFWGYK0D8=GS1.1.1742048096.1.1.1742050979.0.0.0; _conv_v=vi%3A1*sc%3A2*cs%3A1742051552*fs%3A1742048127*pv%3A5*exp%3A%7B1004131246.%7Bv.1004310903-g.%7B100482594.1-100483817.1-100483996.1-100484622.1%7D%7D%7D*seg%3A%7B%7D*ps%3A1742048127; _ga_5C342TC9KC=GS1.1.1742051551.2.1.1742051605.0.0.0; _clsk=1q1gpkx%7C1742051606320%7C15%7C0%7Cq.clarity.ms%2Fcollect");
		headers.put("Origin","chrome-extension://ilehaonighjijnmpnagapkhpcdbhclfg");
		headers.put("Accept","*/*");

		String result =	HttpRequest.post(checkIn)
				.addHeaders(headers)
				.contentType("application/json")
				.body(params).execute().body();
		log.info("签到结果:{}",result);
	}
}
