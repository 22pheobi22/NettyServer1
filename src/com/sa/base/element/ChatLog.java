package com.sa.base.element;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ChatLog {

	// 房间聊天记录
	private TreeMap<String, Long> logPostion = new TreeMap<String, Long>();

	private List<Logs> logsList = new ArrayList<Logs>();

	public TreeMap<String, Long> getLogPostion() {
		return logPostion;
	}

	public void setLogPostion(TreeMap<String, Long> logPostion) {
		this.logPostion = logPostion;
	}

	public List<Logs> getLogsList() {
		return logsList;
	}

	public void setLogsList(List<Logs> logsList) {
		this.logsList = logsList;
	}

}
