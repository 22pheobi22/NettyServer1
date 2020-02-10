package com.sa.service.server;

import com.sa.net.Packet;
import com.sa.net.PacketType;

public class ServerRequestbShareHistory extends Packet {

	@Override
	public void execPacket() {

//		/** 如果有中心 并 目标IP不是中心IP*/
//		if (ConfManager.getIsCenter() && !ConfManager.getCenterIp().equals(this.getRemoteIp())) {
////			this.setOption(1, json);
//			/** 转发给中心*/
//			ServerManager.INSTANCE.sendPacketToCenter(this, Constant.CONSOLE_CODE_TS);
//		} else {
//			/** 根据房间id获取房间内用户信息*/
//			String key = this.getOption(1).toString();
//			String fileIndex = this.getOption(2).toString();
//			String pageIndex = this.getOption(3).toString();
//			List<Object> his = ServerDataPool
//								.serverDataManager
//								.getShareHistory(this.getRoomId(),
//												 this.getOption(1).toString(),
//												 Integer.parseInt(this.getOption(2).toString()),
//												 Integer.parseInt(this.getOption(3).toString()));
//			if (null == his) {
//				return;
//			}
//
//			int index = 0;
//			String json = "";
//			/** 遍历用户信息*/
//			for (int i = 0; i < his.size(); i++) {
//				if (index%Constant.HISTORY_NUM == 0) json="";
//				index++;
//
//				json +=his.get(i)+",";
//
//				if ((index%Constant.HISTORY_NUM == 0 || index==his.size()) && his.get(i).toString().length()> 1) {
//					json = json.substring(0, json.length()-1);
//					/** 实例化获取房间列表 下行 并赋值*/
//					ClientResponebShareHistory crru = new ClientResponebShareHistory(this.getPacketHead());
//					/** json格式的用户信息放入 选项 1 中*/
//					crru.setOption(1, json);
//					crru.setOption(2, key);
//					crru.setOption(3, fileIndex);
//					crru.setOption(4, pageIndex);
//					/** 执行*/
//					crru.execPacket();
//				}
//			}
//		}

	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerRequestbShareHistory;
	}

}