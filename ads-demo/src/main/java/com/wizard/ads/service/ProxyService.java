package com.wizard.ads.service;

import com.wizard.ads.model.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

/**
 * ProxyService类用于处理与代理服务器相关的逻辑。
 * 包含配置代理的方法，使用Selenium WebDriver的ChromeOptions。
 */
@Service
public class ProxyService {

	/**
	 * 配置Chrome浏览器的代理设置。
	 *
	 * @param proxy 包含代理主机和端口的代理信息
	 * @return ChromeOptions 对象，包含代理设置
	 */
	public ChromeOptions configureProxy(Proxy proxy) {
		// 创建SeleniumProxy对象以设置代理
		org.openqa.selenium.Proxy seleniumProxy = new org.openqa.selenium.Proxy();
		seleniumProxy.setHttpProxy(proxy.getHost() + ":" + proxy.getPort()); // 设置HTTP代理
		seleniumProxy.setSslProxy(proxy.getHost() + ":" + proxy.getPort());  // 设置SSL代理

		// 创建ChromeOptions对象并设置代理
		ChromeOptions options = new ChromeOptions();
		options.setProxy(seleniumProxy);
		return options;
	}
}
