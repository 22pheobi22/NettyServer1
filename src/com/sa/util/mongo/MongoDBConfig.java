package com.sa.util.mongo;

/**
 * mongo参数配置类
 * @author bugao.gong
 * @version 创建时间：2019年1月8日 下午5:51:34
 * Pursuing Excelsior!
 */
public class MongoDBConfig {
	/**
	 * IP
	 */
    private String ip;
    /**
     * 端口
     */
    private Integer port = 27017;
    /**
     * 数据库名
     */
    private String db;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    
    private String charset = "UTF-8";
    /**
     * 是否需要用户名密码授权
     */
    private boolean needAuth = false;
    
	public MongoDBConfig( String ip, String db) {
		super();
		this.ip = ip;
		this.db = db;
	}
	
	public MongoDBConfig( String ip, Integer port, String db) {
		super();
		this.ip = ip;
		this.port = port;
		this.db = db;
	}

	public MongoDBConfig(String ip, Integer port, String db, String username, String password) {
		super();
		this.ip = ip;
		this.port = port;
		this.db = db;
		this.username = username;
		this.password = password;
		this.needAuth = true;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public boolean getNeedAuth() {
		return needAuth;
	}

	public void setNeedAuth(boolean needAuth) {
		this.needAuth = needAuth;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}