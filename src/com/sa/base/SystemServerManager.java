package com.sa.base;

import java.util.Map;

import com.sa.net.Packet;
import com.sa.util.StringUtil;

import io.netty.channel.ChannelHandlerContext;

public enum SystemServerManager {
	INSTANCE;

	/** 向通道写消息并发送*/
	private void writeAndFlush(ChannelHandlerContext ctx, Packet pact) throws Exception {
		ctx.writeAndFlush(pact);
	}

	/** 向单一用户发送数据包*/
	public void sendPacketTo(Packet pact) throws Exception {
		// 如果数据包为空 或 数据接收者是中心 返回
		if(pact == null || "0".equals(pact.getToUserId())) return;

		// 获取缓存的 用户-通道信息
		Map<String, ChannelHandlerContext> contextMap  = ServerDataPool.SYSTEM_CHANNEL_MAP;
		// 空则返回
		if(StringUtil.isEmpty(contextMap)) return;

		// 获取接收用户通道
		ChannelHandlerContext targetContext = contextMap.get(pact.getToUserId());
		// 空则返回
		if(targetContext == null) return;

		// 给接收用户发送数据包
		writeAndFlush(targetContext, pact);
	}
}
