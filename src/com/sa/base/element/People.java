/**
 *
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.base.element]
 * 类名称: [People]
 * 类描述: [人员详情缓存]
 * 创建人: [Y.P]
 * 创建时间:[2017年7月15日 上午11:08:50]
 * 修改人: [Y.P]
 * 修改时间:[2017年7月15日 上午11:08:50]
 * 修改备注:[说明本次修改内容]
 * 版本:	 [v1.0]
 *
 */
package com.sa.base.element;

import java.util.HashMap;
import java.util.HashSet;

public class People {
	private String userId = "";
	// 姓名
	private String name = "";
	// 头像
	private String icon = "";
	// 声网ID
	private String agoraId = "";
	
	// 人员角色(管理员、副管理员、主听用户、蹭听用户···)
	private HashSet<String> role = new HashSet<String>();
	// 人员权限(禁言：不许说话;···)
	private HashMap<String, Integer> auth = new HashMap<String, Integer>();

	public HashSet<String> getRole() {
		return role;
	}

	public void setRole(HashSet<String> role) {
		this.role = role;
	}

	public HashMap<String, Integer> getAuth() {
		return auth;
	}

	public void setAuth(HashMap<String, Integer> auth) {
		this.auth = auth;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getAgoraId() {
		return agoraId;
	}

	public void setAgoraId(String agoraId) {
		this.agoraId = agoraId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
