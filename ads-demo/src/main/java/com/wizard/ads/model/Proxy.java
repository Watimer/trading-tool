package com.wizard.ads.model;

import lombok.Data;

/**
 * Proxy类用于表示代理服务器的相关信息。
 * 包含代理主机、端口、用户名和密码等属性。
 */
@Data
public class Proxy {
	// 代理服务器的主机名或IP地址
	private String host;
	// 代理服务器的端口号
	private int port;
	// 代理服务器的用户名（可选）
	private String username;
	// 代理服务器的密码（可选）
	private String password;
}
