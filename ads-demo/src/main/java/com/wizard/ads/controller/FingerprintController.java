package com.wizard.ads.controller;

import com.wizard.ads.model.Fingerprint;
import com.wizard.ads.model.Proxy;
import com.wizard.ads.service.FingerprintService;
import com.wizard.ads.service.ProxyService;
import com.wizard.ads.until.FingerprintGenerator;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FingerprintController类用于处理与指纹和代理相关的HTTP请求。
 * 提供API端点以创建带有指纹和代理的浏览器实例。
 */
@RestController
@RequestMapping("/fingerprint")
public class FingerprintController {

	@Autowired
	private FingerprintService fingerprintService; // 注入指纹服务

	@Autowired
	private ProxyService proxyService; // 注入代理服务

	private static final int WINDOW_WIDTH = 800; // 浏览器窗口宽度
	private static final int WINDOW_HEIGHT = 600; // 浏览器窗口高度

	/**
	 * 创建一个带有指定指纹和代理的Chrome浏览器实例。
	 *
	 * @param fingerprint 包含用户代理、语言和时区的指纹信息
	 * @param proxy       包含代理主机和端口的代理信息
	 * @return WebDriver 实例，表示启动的Chrome浏览器
	 */
	@PostMapping("/create")
	public Map<String, Object> createFingerprint(@RequestBody Fingerprint fingerprint) {
		// 创建带有指纹的浏览器实例
		List<WebDriver> drivers = new ArrayList<>();
		// 配置代理
		//ChromeOptions options = proxyService.configureProxy(proxy);
		// 创建主控页面
		Fingerprint mainFingerprint = FingerprintGenerator.generateRandomFingerprint(); // 生成随机指纹
		WebDriver mainDriver = fingerprintService.createFingerprint(mainFingerprint);
		mainDriver.manage().window().setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT)); // 设置窗口大小
		mainDriver.manage().window().setPosition(new Point(0, 0)); // 设置主控窗口位置
		mainDriver.get("chrome://version/"); // 打开空白页面
		//mainDriver.get("https://browserleaks.com/webrtc");
		drivers.add(mainDriver); // 将主控WebDriver实例添加到列表中

		for (int i = 0; i < 1; i++) {
			int temp = i+1;
			Fingerprint fingerprint1 = FingerprintGenerator.generateRandomFingerprint(); // 生成随机指纹
			WebDriver driver = fingerprintService.createFingerprint(fingerprint1);
			driver.manage().window().setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT)); // 设置窗口大小

			// 设置窗口位置，排列窗口
			int xPosition = (temp % 3) * (WINDOW_WIDTH + 10); // 每行最多3个窗口，10像素间隔
			int yPosition = (temp / 3) * (WINDOW_HEIGHT + 10); // 每行窗口高度加上间隔
			driver.manage().window().setPosition(new Point(xPosition, yPosition)); // 设置窗口位置

			// 打开about:blank页面
			driver.get("chrome://version/");// 执行JavaScript脚本
			//driver.get("https://browserleaks.com/webrtc");

			drivers.add(driver);
		}

		// 返回窗口信息的响应
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Browsers opened successfully");
		response.put("windowCount", 7);
		return response; // 返回包含窗口信息的响应

	}

	/**
	 * 生成显示指纹信息的JavaScript脚本。
	 *
	 * @param fingerprint 指纹信息
	 * @return JavaScript字符串
	 */
	private String generateFingerprintInfoScript(Fingerprint fingerprint) {
		return "document.body.innerHTML = '<h1>Current Fingerprint Information</h1>' + " +
				"'<p><strong>User Agent:</strong> " + fingerprint.getUserAgent() + "</p>' + " +
				"'<p><strong>Language:</strong> " + fingerprint.getLanguage() + "</p>' + " +
				"'<p><strong>Timezone:</strong> " + fingerprint.getTimezone() + "</p>';";
	}

	// 其他API端点可以在这里添加
}
