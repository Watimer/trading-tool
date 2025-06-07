package com.wizard.ads.controller;

import com.wizard.ads.model.Fingerprint;
import com.wizard.ads.service.FingerprintService;
import com.wizard.ads.service.ProxyService;
import com.wizard.ads.until.FingerprintGenerator;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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

	private static final Logger log = LoggerFactory.getLogger(FingerprintController.class);
	static List<WebDriver> drivers = new ArrayList<>();


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
		//browsers.add(mainDriver);

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
			//browsers.add(driver);
		}

		// 创建 JFrame 监听用户操作
		createEventListenerFrame();

		// 监听主窗口操作
		//listenToMainWindow(mainDriver);

		// 创建 JFrame 来捕捉鼠标事件
		//JFrame frame = new JFrame();
		//frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		//frame.setUndecorated(true); // 不显示边框
		//frame.setOpacity(0f); // 完全透明
		//frame.setLocation(0, 0); // 放置在屏幕左上角
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//// 添加鼠标移动监听器
		//frame.addMouseMotionListener(new MouseMotionAdapter() {
		//	@Override
		//	public void mouseMoved(MouseEvent e) {
		//		java.awt.Point mouseLocation = e.getPoint();
		//		// 获取主控窗口的鼠标位置
		//
		//		// 获取主控窗口位置
		//		Point mainWindowPosition = mainDriver.manage().window().getPosition();
		//		mainWindowPosition.move(mouseLocation.x, mouseLocation.y);
		//		//mainWindowPosition.translate(mouseLocation.x, mouseLocation.y); // 更新位置
		//
		//		// 设置其他窗口跟随主控窗口的位置
		//		for (int i = 1; i < drivers.size(); i++) {
		//			WebDriver currentWindow = drivers.get(i);
		//
		//			currentWindow.manage().window().setPosition(new Point(mainWindowPosition.x, mainWindowPosition.y));
		//		}
		//	}
		//});

		// 显示窗体，用于捕捉鼠标事件
		//frame.setVisible(true);


		// 返回窗口信息的响应
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Browsers opened successfully");
		response.put("windowCount", 7);
		return response; // 返回包含窗口信息的响应

	}

	private static void listenToMainWindow(WebDriver mainDriver) {
		String script =
				"window.addEventListener('resize', function() { " +
						"    console.log('Window resized to: ' + window.innerWidth + 'x' + window.innerHeight); " +
						"}); " +
						"window.addEventListener('mousemove', function(event) { " +
						"    console.log('Mouse moved to: ' + event.clientX + ', ' + event.clientY); " +
						"}); " +
						"window.addEventListener('scroll', function() { " +
						"    console.log('Scrolled to: ' + window.scrollY); " +
						"});";

		((JavascriptExecutor) mainDriver).executeScript(script);
	}

	private static void createEventListenerFrame() {
		JFrame frame = new JFrame("主控窗口监听");
		frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		frame.setUndecorated(false);
		//frame.setOpacity(0f); // 完全透明
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());

		JTextField textField = new JTextField(20);
		frame.add(textField);

		// 监听键盘输入
		textField.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char keyChar = e.getKeyChar();
				syncKeyPress(keyChar);
			}
		});

		// 监听鼠标点击
		frame.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				syncMouseClick(x, y);
				log.info("鼠标点击:x:{},y:{}",x,y);
			}
		});

		// 监听鼠标滚动
		frame.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				int scrollAmount = e.getWheelRotation();
				syncMouseScroll(scrollAmount);
				log.info("鼠标移动:x:{},y:{}",scrollAmount);
			}
		});

		frame.setVisible(true);
	}

	// 🔹 同步键盘输入
	private static void syncKeyPress(char keyChar) {
		log.info("键盘输入:{}",keyChar);
		for (WebDriver browser : drivers) {
			JavascriptExecutor js = (JavascriptExecutor) browser;
			String script = "document.activeElement.value += '" + keyChar + "';";
			js.executeScript(script);
			System.out.println(keyChar);
		}
	}

	// 🔹 同步鼠标点击
	private static void syncMouseClick(int x, int y) {
		for (WebDriver browser : drivers) {
			JavascriptExecutor js = (JavascriptExecutor) browser;
			String script = "var event = new MouseEvent('click', {" +
					"bubbles: true, cancelable: true, clientX: " + x + ", clientY: " + y + " });" +
					"document.elementFromPoint(" + x + ", " + y + ").dispatchEvent(event);";
			js.executeScript(script);
		}
	}

	// 🔹 同步鼠标滚动
	private static void syncMouseScroll(int scrollAmount) {
		for (WebDriver browser : drivers) {
			JavascriptExecutor js = (JavascriptExecutor) browser;
			String script = "window.scrollBy(0, " + (scrollAmount * 50) + ");";
			js.executeScript(script);
		}
	}
}
