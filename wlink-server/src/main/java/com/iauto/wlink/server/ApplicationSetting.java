package com.iauto.wlink.server;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

public class ApplicationSetting {

	/** 配置项目 */
	private final static String PRO_PORT = "server.port";
	private final static String PRO_HEARTBEAT_INTERVAL = "server.hearbeat.interval";
	private final static String PRO_MQ_URL = "server.message.mq.url";
	private final static String PRO_HMAC_KEY = "server.hmac.sha256.key";
	private final static String PRO_SSL_ENABLED = "server.ssl.enabled";
	private final static String PRO_SSL_CRT_FILE_NAME = "server.ssl.crt.file.name";
	private final static String PRO_SSL_PK_FILE_NAME = "server.ssl.pk.file.name";

	/** 单例 */
	private final static ApplicationSetting instance = new ApplicationSetting();

	/** 应用设置 */
	private final Properties settings = new Properties();

	/** 配置默认值 */
	private final static int DEFAULT_PORT = 2391;
	private final static int DEFAULT_HEARTBEAT_INTERVAL = 50;
	private final static boolean DEFAULT_IS_SSL_ENABLED = false;
	private final static String DEFAULT_CRT_FILE_NAME = "wlink-server.crt";
	private final static String DEFAULT_PK_FILE_NAME = "wlink-server.key";

	private ApplicationSetting() {
	}

	public static ApplicationSetting getInstance() {
		return instance;
	}

	/**
	 * 加载应用配置项
	 * 
	 * @throws IOException
	 */
	public void load() throws IOException {
		settings.load( this.getClass().getClassLoader().getResourceAsStream( "application.properties" ) );
	}

	/**
	 * 获取应用配置中服务监听端口
	 * 
	 * @return 监听端口
	 */
	public int getPort() {
		String value = settings.getProperty( PRO_PORT );
		int port = DEFAULT_PORT;
		try {
			port = Integer.valueOf( value );
		} catch ( Exception ex ) {
			// ignore
		}
		return port;
	}

	/**
	 * 获取心跳检测的间隔时间
	 * 
	 * @return 心跳间隔时间
	 */
	public int getHeartbeatInterval() {
		String value = settings.getProperty( PRO_HEARTBEAT_INTERVAL );
		int interval = DEFAULT_HEARTBEAT_INTERVAL;
		try {
			interval = Integer.valueOf( value );
		} catch ( Exception ex ) {
			// ignore
		}
		return interval;
	}

	/**
	 * 获取MQ的URL
	 * 
	 * @return URL
	 */
	public String getMqUrl() {
		String url = settings.getProperty( PRO_MQ_URL );
		return url;
	}

	/**
	 * 获取加密密匙
	 * 
	 * @return 密匙
	 */
	public String getHmacKey() {
		String key = settings.getProperty( PRO_HMAC_KEY );
		return key;
	}

	/**
	 * 是否采用加密安全传输
	 * 
	 * @return true 采用加密传输；false 不采用
	 */
	public boolean isSSLEnabled() {
		String value = settings.getProperty( PRO_SSL_ENABLED );
		boolean isEnabled = DEFAULT_IS_SSL_ENABLED;

		try {
			isEnabled = Boolean.valueOf( value );
		} catch ( Exception ex ) {
			// ignore
		}
		return isEnabled;
	}

	/**
	 * 获取证书文件名
	 * 
	 * @return 证书文件名
	 */
	public String getCrtFileName() {
		String value = settings.getProperty( PRO_SSL_CRT_FILE_NAME );
		String name = StringUtils.isNotBlank( value ) ? value : DEFAULT_CRT_FILE_NAME;
		return name;
	}

	/**
	 * 获取私匙文件名
	 * 
	 * @return 私匙文件名
	 */
	public String getPkFileName() {
		String value = settings.getProperty( PRO_SSL_PK_FILE_NAME );
		String name = StringUtils.isNotBlank( value ) ? value : DEFAULT_PK_FILE_NAME;
		return name;
	}
	
	public String getKeyPassword() {
		return StringUtils.EMPTY;
	}
}
