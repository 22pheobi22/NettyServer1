/**
 * 登出 上行
 */
package com.sa.service.sys;

import com.sa.net.Packet;
import com.sa.net.PacketType;

public class SysLoginOutReq extends Packet{
	public SysLoginOutReq(){}

	@Override
	public PacketType getPacketType() {
		return PacketType.SysLoginOutReq;
	}

	@Override
	public void execPacket() {
		/** 实例化登出 下行 并执行*/
		new SysLoginOutRes(this.getPacketHead(), this.getOptions()).execPacket();
	}

}
