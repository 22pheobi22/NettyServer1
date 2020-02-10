/**
 *
 * 项目名称：NettyServer
 * 类名称：SystemLoginManager
 * 类描述：
 * 创建人：Y.P
 * 创建时间：2019年1月29日 下午2:50:37
 * 修改人：Y.P
 * 修改时间：2019年1月29日 下午2:50:37
 * @version  1.0
 *
 */
package com.sa.service.manager;

import com.sa.base.ServerDataPool;
import com.sa.service.sys.SysLoginReq;
import com.sa.service.sys.SysLoginRes;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author Y.P
 *
 */
public enum SystemLoginManager {
	INSTANCE;

	public void login(ChannelHandlerContext context, SysLoginReq loginPact) {
		ServerDataPool.SYSTEM_CHANNEL_MAP.put(loginPact.getFromUserId(), context);
		ServerDataPool.CHANNEL_SYSTEM_MAP.put(context, loginPact.getFromUserId());

		ServerDataPool.TEMP_CONN_MAP.remove(context);

		SysLoginRes SysLoginRes = new SysLoginRes();
		SysLoginRes.setPacketHead(loginPact.getPacketHead());
		SysLoginRes.setOptions(loginPact.getOptions());
		
		SysLoginRes.execPacket();
	}
}