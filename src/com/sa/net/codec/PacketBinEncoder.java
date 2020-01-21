/**
 * 
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.net.codec]
 * 类名称: [PacketBinEncoder]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月13日 下午6:18:09]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月13日 下午6:18:09]
 * 修改备注:[说明本次修改内容]  
 * 版本:	 [v1.0]   
 * 
 */
package com.sa.net.codec;

import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import com.sa.net.Packet;
import com.sa.util.ByteBufUtil;

public class PacketBinEncoder {
	public BinaryWebSocketFrame encode(Packet msg)
			throws Exception {
		ByteBufUtil byteBufUtil = new ByteBufUtil();

		byteBufUtil.writeInt(msg.getPacketType().getType());
		msg.writePacketHead(byteBufUtil);
		msg.writePacketBody(byteBufUtil);
		
		BinaryWebSocketFrame bin = new BinaryWebSocketFrame();
		bin.content().writeBytes(byteBufUtil.getBytes());

		return bin;
	}
}
