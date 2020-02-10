package com.sa.base.element;

public class Logs {
	private String userId = ""; // 用户id
	private String ico = ""; // 头像
	private String name = ""; // 发信人名称
	private String chat = ""; // 信息内容
	private Long sendTime = 0L; // 发送时间
	private int transactionId = 0; // 事务id

	public Logs() {}

	public Logs(String userId, String ico, String name, String chat, Long sendTime, int transactionId) {
		this.userId = userId;
		this.ico = ico;
		this.name = name;
		this.chat = chat;
		this.sendTime = sendTime;
		this.transactionId = transactionId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getChat() {
		return chat;
	}
	public void setChat(String chat) {
		this.chat = chat;
	}
	public Long getSendTime() {
		return sendTime;
	}
	public void setSendTime(Long sendTime) {
		this.sendTime = sendTime;
	}
	public String getIco() {
		return ico;
	}
	public void setIco(String ico) {
		this.ico = ico;
	}
	public int getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}