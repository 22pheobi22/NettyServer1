/**
 * 
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.net.codec]
 * 类名称: [PacketBinDecoder]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月13日 下午1:21:38]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月13日 下午1:21:38]
 * 修改备注:[说明本次修改内容]  
 * 版本:	 [v1.0]   
 * 
 */
package com.sa.net.codec;

import com.sa.net.Packet;
import com.sa.net.PacketManager;
import com.sa.util.ByteBufUtil;

public class PacketBinDecoder {
	public Object decode(ByteBufUtil byteBufUtil) throws Exception {
		if(byteBufUtil.length <= 0) return null ;

		short packetType = (short) byteBufUtil.readInt();
		Integer transactionId = byteBufUtil.readInt();
		String roomId = byteBufUtil.readStr();
		String fromUserId = byteBufUtil.readStr();
		String toUserId = byteBufUtil.readStr();
		Integer status = byteBufUtil.readInt();

		Packet packet = PacketManager.INSTANCE.createNewPacket(packetType, transactionId, roomId, fromUserId, toUserId, status);

//		boolean useCompression = packet.isUseCompression();
//		ByteBuf realBuf = decompression(frame,useCompression);

		packet.readPacketBody(byteBufUtil);

		return packet;
	}
}