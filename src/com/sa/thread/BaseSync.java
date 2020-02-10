package com.sa.thread;

import java.util.HashMap;
import java.util.Map;

import com.sa.util.HttpClientUtil;

public abstract class BaseSync implements Runnable {

	private String url = null;
	private int time = 5;

	public BaseSync() {}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public BaseSync(String url, int time) {
		this.url = url;
		this.time = time;
	}

	@Override
	public abstract void run();

	public String post(String json, String sign) {

		Map<String, String> params = new HashMap<>();
		params.put("info", json);
		params.put("sign", sign);
		String rs = HttpClientUtil.post(url, params);

        return rs;
	}

	public abstract String toJson();

}