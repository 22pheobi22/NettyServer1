package com.sa.service.client;

import com.sa.base.ServerDataPool;
import com.sa.net.Packet;
import com.sa.net.PacketHeadInfo;
import com.sa.net.PacketType;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class ClientResponehRoomUser extends Packet {

	/**
	 *
	 */
	public ClientResponehRoomUser() {}

	/**
	 * @param transactionId
	 * @param roomId
	 * @param fromUserId
	 * @param toUserId
	 * @param status
	 */
	public ClientResponehRoomUser(PacketHeadInfo packetHead) {
		this.setPacketHead(packetHead);
	}

	@Override
	public void execPacket() {
		sendrs();
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ClientResponehRoomUser;
	}

	private void sendrs() {
		String res = (String) this.getOption(1);
		HttpRequest request = ServerDataPool.USER_REQUEST_MAP.get(this.getFromUserId());
		ChannelHandlerContext ctx = ServerDataPool.USER_CHANNEL_MAP_BACK.get(this.getFromUserId());
		try {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(res.getBytes("UTF-8")));
	        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
	        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH,
	                response.content().readableBytes());
	        if (HttpHeaders.isKeepAlive(request)) {
	            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
	        }
	        if(null!=ctx){
	        	ctx.write(response);
		        ctx.flush();
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
