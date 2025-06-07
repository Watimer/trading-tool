package com.wizard.ads.service;

import com.wizard.ads.model.Fingerprint;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * FingerprintService类用于处理与浏览器指纹相关的逻辑。
 * 包含创建指纹的方法，使用Selenium WebDriver启动Chrome浏览器。
 */
@Service
public class FingerprintService {

	/**
	 * 创建一个带有指定指纹的Chrome浏览器实例。
	 *
	 * @param fingerprint 包含用户代理、语言和时区的指纹信息
	 * @return WebDriver 实例，表示启动的Chrome浏览器
	 */
	public WebDriver createFingerprint(Fingerprint fingerprint) {
		// 设置ChromeDriver的路径
		System.setProperty("webdriver.chrome.driver", "/Users/yueyaoli/Downloads/chromedriver_mac64/chromedriver");

		// 创建ChromeOptions对象以配置浏览器选项
		ChromeOptions options = new ChromeOptions();
		options.addArguments("user-agent=" + fingerprint.getUserAgent()); // 设置用户代理
		options.addArguments("--lang=" + fingerprint.getLanguage());     // 设置语言
		options.addArguments("--timezone=" + fingerprint.getTimezone()); // 设置时区
		// 禁用GPU加速
		options.addArguments("--disable-gpu");
		// 禁用WebRTC
		//options.addArguments("--disable-webrtc");
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("webrtc.ip_handling_policy", "disable_non_proxied_udp"); // 禁用UDP以防止WebRTC泄露
		prefs.put("webrtc.multiple_routes_enabled", false); // 禁用多个路由
		prefs.put("webrtc.nonproxied_udp_enabled", false); // 禁用非代理 UDP
		options.setExperimentalOption("prefs", prefs);

		options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation")); // 禁用自动化控制信息
		//options.addArguments("--disable-infobars");
		//options.addExtensions(new File("/Users/yueyaoli/Library/Application Support/Google/Chrome/Default/Extensions/bkhaagjahfmjljalopjnoealnfndnagc/8.0.1_0.crx"));
		//Proxy proxy = new Proxy();
		//proxy.setHost("171.236.171.211");
		//proxy.setPort(47927);
		//proxy.setUsername("hADVLI");
		//proxy.setPassword("RIybIj");
		//String socks5Proxy ="socks5://hADVLI:RIybIj@171.236.171.211:47927";
		//Proxy proxy = new Proxy();
		//proxy.setSocksProxy(socks5Proxy);
		//options.setProxy(proxy);
		//options.addArguments("--proxy-server=https://171.236.171.211:47927");
		//options.addArguments("--load-extension=/Users/yueyaoli/wizard/chajian");
		//options.addArguments("--disable-extensions-file-verification");

		options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
		options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
		options.setExperimentalOption("useAutomationExtension", false);

		// 加载并授权 Chrome 插件
		try {
			String encodedExtension = encodeExtension("/Users/yueyaoli/Library/Application Support/Google/Chrome/Default/Extensions/bkhaagjahfmjljalopjnoealnfndnagc/8.0.1_0.crx");
			options.addEncodedExtensions(encodedExtension);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 创建并返回ChromeDriver实例
		WebDriver driver = new ChromeDriver(options);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		// 伪装 navigator.webdriver
		js.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

		// 伪装 navigator.plugins 和 mimeTypes
		js.executeScript("Object.defineProperty(navigator, 'plugins', {get: () => [1, 2, 3]})");
		js.executeScript("Object.defineProperty(navigator, 'mimeTypes', {get: () => [1, 2, 3]})");

		// 伪装 Chrome 环境
		js.executeScript("window.chrome = {runtime: {}}");

		// 伪装 WebGL 指纹
		js.executeScript("WebGLRenderingContext.prototype.getParameter = (param) => param === 37445 ? 'NVIDIA GeForce RTX 3080' : 'Intel HD Graphics';");
		return driver;
	}

	private String encodeExtension(String extensionPath) throws IOException {
		byte[] extensionBytes = Files.readAllBytes(new File(extensionPath).toPath());
		return Base64.getEncoder().encodeToString(extensionBytes);
	}
}
