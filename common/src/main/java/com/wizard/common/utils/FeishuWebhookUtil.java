package com.wizard.common.utils;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wizard
 * @date 2024-09-20
 * @desc
 */
public class FeishuWebhookUtil {

	/**
	 * 上传图片并获取 image_key
	 *
	 * @param accessToken 飞书 API 访问令牌
	 * @param imagePath 本地图片路径
	 * @return 上传后的 image_key
	 */
	public String uploadImage(String accessToken, String imagePath) {
		String uploadUrl = "https://open.feishu.cn/open-apis/image/v4/put/";

		// 读取本地图片并构造 multipart/form-data 请求

		// 设置 HTTP 头
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.set("Authorization", "Bearer " + accessToken);

		// 构建 Multipart 请求体
		// 这里你需要构造 multipart/form-data 形式的请求体，上传图片

		// RestTemplate 发送请求
		// 返回 image_key
		return "your_image_key"; // 返回实际获取的 image_key
	}

	/**
	 * 发送图片消息到飞书群
	 *
	 * @param imageKey 飞书上传图片后的 image_key
	 */
	public void sendImageToFeishu(String imageKey,String WEBHOOK_URL) {
		// 构建消息内容
		Map<String, Object> message = new HashMap<>();
		Map<String, Object> content = new HashMap<>();
		Map<String, Object> image = new HashMap<>();

		// 设置消息类型为图片
		message.put("msg_type", "image");
		// 设置 image_key
		image.put("image_key", imageKey);
		content.put("image", image);
		message.put("content", content);

		// 设置 HTTP 头
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// 发送 POST 请求
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);
		ResponseEntity<String> response = restTemplate.exchange(WEBHOOK_URL, HttpMethod.POST, request, String.class);

		// 打印响应
		System.out.println("Response: " + response.getBody());
	}
}
