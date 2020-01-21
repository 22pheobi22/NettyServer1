/**
 *
 * 项目名称：NettyServer
 * 类名称：Channel
 * 类描述：
 * 创建人：Y.P
 * 创建时间：2019年3月8日 上午10:47:20
 * 修改人：Y.P
 * 修改时间：2019年3月8日 上午10:47:20
 * @version  1.0
 *
 */
package com.sa.base.element;

public class ChannelExtend {
	// 0:客户端；1：websoket； 
	private int channelType = 0;
	
	private String userId = null;
	
	private Long connBeginTime = System.currentTimeMillis();
	
	public ChannelExtend(){}

	public ChannelExtend(String userId){
		this.userId = userId;
	}
	public ChannelExtend(int channelType){
		this.channelType = channelType;
	}
	public ChannelExtend(String userId, int channelType){
		this.userId = userId;
		this.channelType = channelType;
	}

	public int getChannelType() {
		return channelType;
	}
	public void setChannelType(int channelType) {
		this.channelType = channelType;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Long getConnBeginTime() {
		return connBeginTime;
	}
	public void setConnBeginTime(Long connBeginTime) {
		this.connBeginTime = connBeginTime;
	}
}
