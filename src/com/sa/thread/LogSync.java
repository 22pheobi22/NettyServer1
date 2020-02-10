/**
 *
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.thread]
 * 类名称: [LogSync]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月11日 下午5:31:45]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月11日 下午5:31:45]
 * 修改备注:[说明本次修改内容]
 * 版本:	 [v1.0]
 *
 */
package com.sa.thread;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import com.sa.base.ConfManager;
import com.sa.base.ServerDataPool;
import com.sa.net.Packet;
import com.sa.util.MD5Util;

public class LogSync extends BaseSync {

	public LogSync() {}

	public LogSync(String url, int time) {
		super(url, time);
	}

	@Override
	public void run() {
		while(true) {
			if (null != super.getUrl() && !"".equals(super.getUrl())) {
				try {
					String json = toJson();

					String key = ConfManager.getMd5Key();
					String sign = MD5Util.MD5("nettyLogShare"+key);
					json = URLEncoder.encode(json, "utf-8");

					post(json, sign);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			try {
				Thread.sleep(1000*60*super.getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String toJson() {
		String strJson = "";
		if (ServerDataPool.log.size() > 0) {
			Map<String, Packet> logs = ServerDataPool.log;
			strJson = "[";
			for (Entry<String, Packet> log : logs.entrySet()) {
				log.getValue().getPacketHead().getStatus();

				String packetBody = "";

				for (Entry<Integer, Object> content : log.getValue().getOptions().entrySet()) {
					String value = (String) content.getValue();
					if (null != value && !value.equals("")) {
						try {
							packetBody += "{\"" + content.getKey() + "\":\"" + URLEncoder.encode(value, "utf-8") + "\"},";
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				if (packetBody.length() > 0) {
					packetBody = packetBody.substring(0, packetBody.length() - 1);
				}

				strJson += "{\"" + log.getKey() + "\"" + ":"// token
								+ "{\"packetType\":" + "\"" + log.getValue().getPacketType() + "\"" + ","
								+ "\"packetHead\":{"
										+ "\"fromUserId\":" + "\"" + log.getValue().getFromUserId() + "\"" + ","
										+ "\"roomId\":" + "\"" + log.getValue().getRoomId() + "\"" + ","
										+ "\"toUserIdId\":" + "\"" + log.getValue().getToUserId()  + "\""+ ","
										+ "\"transactionId\":" + "\"" + log.getValue().getTransactionId() + "\"" + ","
										+ "\"status\":" + "\"" + log.getValue().getStatus() + "\""
								+ "},"
								+ "\"packetBody\":["
												+ packetBody

								+ "]"
								+ "}"
						+ "},";
				ServerDataPool.log.remove(log.getKey());
			}
			strJson = strJson.substring(0, strJson.length() - 1) + "]";
		}

		return strJson;
	}
}