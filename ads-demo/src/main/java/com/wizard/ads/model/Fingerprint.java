package com.wizard.ads.model;

import lombok.Data;

/**
 * Fingerprint类用于表示浏览器指纹的相关信息。
 * 包含用户代理、语言和时区等属性。
 */
@Data
public class Fingerprint {
	// 浏览器用户代理字符串
	private String userAgent;
	// 浏览器语言设置
	private String language;
	// 浏览器时区设置
	private String timezone;
}
