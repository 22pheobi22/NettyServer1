package com.sa.service.sys;

import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.util.ReadConf;

/**
 *
 * @author heyi
 * @date 2019年2月27日 下午4:24:08
 * 
 *
 */
public class SysReloadConfigReq extends Packet {
	public SysReloadConfigReq(){}

	@Override
	public void execPacket() {
		new ReadConf().readFileByLines();
		SysReloadConfigRes sysReloadConfigRes = new SysReloadConfigRes();
		sysReloadConfigRes.setPacketHead(this.getPacketHead());
		sysReloadConfigRes.setOption(254, "重新加载配置成功");
		sysReloadConfigRes.execPacket();
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.SysReloadConfigReq;
	}

}