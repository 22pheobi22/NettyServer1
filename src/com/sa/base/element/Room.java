/**
 *
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.base.element]
 * 类名称: [ROOM]
 * 类描述: [房间详情缓存]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月15日 上午10:53:32]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月15日 上午10:53:32]
 * 修改备注:[说明本次修改内容]
 * 版本:	 [v1.0]
 *
 */
package com.sa.base.element;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Room {
	// 房间人数
	private int peopleNum = 0;

	// 房间人员
	private Map<String, People> peoples = new ConcurrentHashMap<String, People>();

	private Map<String, Integer> notSpeakPeoples = new ConcurrentHashMap<String, Integer>();

	private ChatLog chatLog = new ChatLog();

	// 房间初始化禁止操作角色
	private HashSet<String> notDoRole = new HashSet<String>();

	/** 共享对象*/
	public Map<String, Share> share = new ConcurrentHashMap<>();

	public int getPeopleNum() {
		return peopleNum;
	}

	public void setPeopleNum(int peopleNum) {
		this.peopleNum = peopleNum;
	}

	public Map<String, People> getPeoples() {
		return peoples;
	}

	public void setPeoples(Map<String, People> peoples) {
		this.peoples = peoples;
	}

	public HashSet<String> getNotDoRole() {
		return notDoRole;
	}

	public void setNotDoRole(HashSet<String> notDoRole) {
		this.notDoRole = notDoRole;
	}

	public ChatLog getChatLog() {
		return chatLog;
	}

	public void setChatLog(ChatLog chatLog) {
		this.chatLog = chatLog;
	}

	public Map<String, Integer> getNotSpeakPeoples() {
		return notSpeakPeoples;
	}

	public void setNotSpeakPeoples(Map<String, Integer> notSpeakPeoples) {
		this.notSpeakPeoples = notSpeakPeoples;
	}

	public Map<String, Share> getShare() {
		return share;
	}

	public void setShare(Map<String, Share> share) {
		this.share = share;
	}

}
