/**
 *
 * 项目名称：CommunicationManage
 * 类名称：SysCloseRoomsReq
 * 类描述：
 * 创建人：Y.P
 * 创建时间：2019年2月12日 下午3:36:20
 * 修改人：Y.P
 * 修改时间：2019年2月12日 下午3:36:20
 * @version  1.0
 *
 */
package com.sa.service.sys;

import com.sa.net.Packet;
import com.sa.net.PacketType;
import com.sa.service.server.ServerRequestbRoomUser;

public class SysUpdRoomReq extends Packet {

	public SysUpdRoomReq(){
	}

	@Override
	public void execPacket() {
		String stuIds = (String) this.getOption(3);
		if (null != stuIds) {
			String[] arr = stuIds.split(",");
			for (String tmp : arr) {
				ServerRequestbRoomUser serverRequestbRoomUser = new ServerRequestbRoomUser();
				serverRequestbRoomUser.setPacketHead(this.getPacketHead());
				serverRequestbRoomUser.setRoomId((String) this.getOption(2));
				serverRequestbRoomUser.setFromUserId(tmp);

				serverRequestbRoomUser.execPacket();
			}
		}
		
		SysUpdRoomRes sysUpdRoomRes = new SysUpdRoomRes();
		sysUpdRoomRes.setPacketHead(this.getPacketHead());
		sysUpdRoomRes.setOption(254, "推送学员列表成功");
		sysUpdRoomRes.execPacket();
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.SysUpdRoomReq;
	}

}