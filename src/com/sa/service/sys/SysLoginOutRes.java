package com.sa.service.sys;

import java.util.TreeMap;

import com.sa.base.ServerDataPool;
import com.sa.base.SystemServerManager;
import com.sa.net.Packet;
import com.sa.net.PacketHeadInfo;
import com.sa.net.PacketType;

import io.netty.channel.ChannelHandlerContext;

public class SysLoginOutRes extends Packet {

	public SysLoginOutRes() {}

	public SysLoginOutRes(PacketHeadInfo packetHead) {
		this.setPacketHead(packetHead);
	}

	public SysLoginOutRes(PacketHeadInfo packetHead, TreeMap<Integer, Object> options) {
		this.setPacketHead(packetHead);
		this.setOptions(options);
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.SysLoginOutRes;
	}

	@Override
	public void execPacket() {
		try {
			SystemServerManager.INSTANCE.sendPacketTo(this);

			ChannelHandlerContext ctx =  ServerDataPool.SYSTEM_CHANNEL_MAP.get(this.getFromUserId());
			if(null!=ctx){
				ctx.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
