package com.sa.service.client;

import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.util.ByteBufUtil;

public class ClientHeartBeat extends Packet{
	public ClientHeartBeat(){}

	@Override
	public void writePacketBody(ByteBufUtil buf) {
	}

	@Override
	public void readPacketBody(ByteBufUtil buf) {
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ClientHeartBeat;
	}

	@Override
	public void execPacket() {
System.out.println("心跳client");
	}

	@Override
	public String toString() {
		return null;
	}

}
