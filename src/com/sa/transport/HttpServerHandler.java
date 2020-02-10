/**
 *
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.transport]
 * 类名称: [HttpHandler]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2017年8月2日 上午11:54:05]
 * 修改人: [Y.P]
 * 修改时间:[2017年8月2日 上午11:54:05]
 * 修改备注:[说明本次修改内容]
 * 版本:	 [v1.0]
 *
 */
package com.sa.transport;

import java.net.InetAddress;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.sa.base.ConfManager;
import com.sa.base.ServerDataPool;
import com.sa.service.server.ServerRequestcRemove;
import com.sa.service.server.ServerRequestcRoomRemove;
import com.sa.service.server.ServerRequesthRoomUser;
import com.sa.util.HMACSHA1Util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {
	private HttpRequest request;

	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest) {
            request = (HttpRequest) msg;
            String uri = request.getUri();
            uri = uri.substring(1).trim();
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            String parm = buf.toString(io.netty.util.CharsetUtil.UTF_8);
            buf.release();

            Map<String, String> parMap = decodeRs(parm);

            if (parMap.size() > 0
            		&& null != parMap.get("reqType")
            		&& null != parMap.get("sign")
            		&& null != parMap.get("roomId")) {

            	String reqType = com.sa.util.Security.decrypt(parMap.get("reqType"));
            	String sign = URLDecoder.decode(parMap.get("sign"));
            	String bodySize = parMap.get("bodySize");
            	String mySign = HMACSHA1Util.getHmacSHA1(parMap.get("reqType") + parMap.get("roomId") + bodySize, ConfManager.getSignKey());

            	long currentSize = System.currentTimeMillis();

            	if (mySign.equals(sign) && (currentSize - Long.parseLong(bodySize)) <= ConfManager.getSignOverTime()) {
            		switch (Integer.parseInt(reqType)) {
						case 1:
							/** 获取 用户列表*/
							String roomId = parMap.get("roomId");
							String fromUserId = parMap.get("userId");
							String toUserId = parMap.get("userId");
							getUsers(fromUserId, toUserId, roomId, ctx);
							break;
						case 2:
							/** 踢人*/
							String roomId1 = parMap.get("roomId");
							String fromUserId1 = parMap.get("fromUserId");
							String toUserId1 = parMap.get("toUserId");
							kickUser(roomId1, fromUserId1, toUserId1, ctx);
							break;
						case 3:
							/** 销毁 聊天室*/
							String roomId2 = parMap.get("roomId");
							String fromUserId2 = parMap.get("fromUserId");
							String toUserId2 = parMap.get("fromUserId");
							delRoom(roomId2, fromUserId2, toUserId2, ctx);
							break;
						default:
							break;
            		}
				}

			}

        }
    }

	@Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

    /** 解码 参数*/
    private Map<String, String> decodeRs(String parm) {
    	Map<String, String> map = new HashMap<>();
        try {
            String[] parmArr = parm.split("&");
            for (String par : parmArr) {
            	if (par.contains("=")) {
            		String[] finParArr = par.split("=");
                	map.put(finParArr[0], finParArr[1]);
				} else {
					map.put(par, "");
				}
			}
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return map;
    }

    /** 获取 用户列表*/
    private void getUsers(String fromUserId, String toUserId, String roomId, ChannelHandlerContext ctx) {
    	String rmIp = getLocalIp();
    	ServerDataPool.USER_CHANNEL_MAP_BACK.put(fromUserId, ctx);
    	ServerDataPool.USER_REQUEST_MAP.put(fromUserId, request);

    	ServerRequesthRoomUser srru = new ServerRequesthRoomUser(31415926, roomId, fromUserId, toUserId, 0);
		srru.setRemoteIp(rmIp);
		srru.execPacket();
	}

    /**获取 本机IP*/
	private String getLocalIp() {
		String ip = "";
		InetAddress ia=null;
        try {
            ia=ia.getLocalHost();
            String localip=ia.getHostAddress();
            ip = localip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
	}

	private void kickUser(String roomId, String fromUserId, String toUserId, ChannelHandlerContext ctx) {
		ServerRequestcRemove srr = new ServerRequestcRemove(31415928, roomId, fromUserId, toUserId, 0);
		srr.execPacket();
		String res = "踢人成功！";
		noticeBack(ctx, res);
	}

	private void delRoom(String roomId, String fromUserId, String toUserId, ChannelHandlerContext ctx) {
		ServerRequestcRoomRemove srr = new ServerRequestcRoomRemove(31415969, roomId, fromUserId, toUserId, 0);
		srr.execPacket();
		String res = "删除聊天室成功！";
		noticeBack(ctx, res);
	}

	private void noticeBack(ChannelHandlerContext ctx, String res) {

		try {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(res.getBytes("UTF-8")));
	        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
	        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH,
	                response.content().readableBytes());
	        if (HttpHeaders.isKeepAlive(request)) {
	            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
	        }
	        ctx.write(response);
	        ctx.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}