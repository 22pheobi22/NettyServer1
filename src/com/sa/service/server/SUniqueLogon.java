/**
 * 
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.service.server]
 * 类名称: [UniqueLogon]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月17日 下午3:03:21]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月17日 下午3:03:21]
 * 修改备注:[说明本次修改内容]  
 * 版本:	 [v1.0]   
 * 
 */
package com.sa.service.server;

import com.sa.net.Packet;
import com.sa.net.PacketHeadInfo;
import com.sa.net.PacketType;

public class SUniqueLogon extends Packet {
	public SUniqueLogon(){}

	public SUniqueLogon(PacketHeadInfo packetHead) {
		this.setPacketHead(packetHead);
	}

	@Override
	public void execPacket() {
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.SUniqueLogon;
	}

}