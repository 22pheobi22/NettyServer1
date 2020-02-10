package com.sa.service.sys;

import com.sa.base.SystemServerManager;
import com.sa.net.Packet;
import com.sa.net.PacketType;

public class SysGetRoomStudentsRes extends Packet {

	@Override
	public void execPacket() {
		try {
			SystemServerManager.INSTANCE.sendPacketTo(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.SysGetRoomStudentsRes;
	}

}