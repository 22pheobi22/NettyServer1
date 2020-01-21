package com.sa.service.server;

import com.sa.net.Packet;
import com.sa.net.PacketType;

public class ServerHeartBeat extends Packet{

	public ServerHeartBeat() {
		this.setTransactionId(0);
		this.setRoomId("0");
		this.setFromUserId("0");
		this.setToUserId("0");;
		this.setStatus(0);
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerHearBeat;
	}

	@Override
	public void execPacket() {
		System.err.println("收到客户端心跳事件！");
	}

	@Override
	public String toString() {
		return null;
	}

}
