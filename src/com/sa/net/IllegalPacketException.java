package com.sa.net;

public class IllegalPacketException  extends RuntimeException{

	// 非法数据包异常
	public IllegalPacketException() {
		super();
	}

	public IllegalPacketException(String message) {
		super(message);
	}
}