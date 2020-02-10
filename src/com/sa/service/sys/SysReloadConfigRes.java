package com.sa.service.sys;

import com.sa.base.SystemServerManager;
import com.sa.net.Packet;
import com.sa.net.PacketType;

/**
 *
 * @author heyi
 * @date 2019年2月27日 下午4:26:15
 * 
 *
 */
public class SysReloadConfigRes extends Packet {
	public SysReloadConfigRes(){}

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
		return PacketType.SysReloadConfigRes;
	}

}