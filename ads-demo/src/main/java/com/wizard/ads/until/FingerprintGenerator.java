package com.wizard.ads.until;

import com.alibaba.fastjson.JSONObject;
import com.wizard.ads.model.Fingerprint;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @author 岳耀栎
 * @date 2025-03-14
 * @desc
 */
@Slf4j
public class FingerprintGenerator {
	private static final String[] USER_AGENTS = {
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Safari/605.1.15",
			"Mozilla/5.0 (Linux; Android 10; SM-G973F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Mobile Safari/537.36",
			"Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1",
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3",
			// 可以添加更多用户代理
	};

	private static final String[] LANGUAGES = {
			"en-US", "fr-FR", "de-DE", "es-ES", "zh-CN", "ja-JP", "ru-RU"
	};

	private static final String[] TIMEZONES = {
			"America/New_York", "Europe/London", "Asia/Shanghai", "Asia/Tokyo", "Australia/Sydney"
	};

	private static final Random RANDOM = new Random();

	/**
	 * 生成随机的Fingerprint对象。
	 *
	 * @return 随机生成的Fingerprint对象
	 */
	public static Fingerprint generateRandomFingerprint() {
		Fingerprint fingerprint = new Fingerprint();
		fingerprint.setUserAgent(USER_AGENTS[RANDOM.nextInt(USER_AGENTS.length)]); // 随机选择用户代理
		fingerprint.setLanguage(LANGUAGES[RANDOM.nextInt(LANGUAGES.length)]); // 随机选择语言
		fingerprint.setTimezone(TIMEZONES[RANDOM.nextInt(TIMEZONES.length)]); // 随机选择时区
		log.info("生成浏览器指纹信息:{}", JSONObject.toJSONString(fingerprint));
		return fingerprint;
	}

}
