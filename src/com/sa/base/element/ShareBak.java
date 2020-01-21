package com.sa.base.element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShareBak {
	// 第一个 Integer 代表文档索引， 第二个 Integer 代表 页码 索引 ， 第三个 Object 代表 历史
	private Map<Integer, Map<Integer, List<Object>>> shareHistory = new ConcurrentHashMap<>();
	// 对象内容
	private Object shareContent = null;
	
	private HashMap<String, Object> map = new HashMap<>();

	public Object getShareContent() {
		return shareContent;
	}

	public void setShareContent(Object shareContent) {
		this.shareContent = shareContent;
	}

	public Map<Integer, Map<Integer, List<Object>>> getShareHistory() {
		return shareHistory;
	}

	public void setShareHistory(Map<Integer, Map<Integer, List<Object>>> shareHistory) {
		this.shareHistory = shareHistory;
	}

	public HashMap<String, Object> getMap() {
		return map;
	}

	public void setMap(HashMap<String, Object> map) {
		this.map = map;
	}

}
