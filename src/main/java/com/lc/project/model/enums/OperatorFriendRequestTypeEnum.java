package com.lc.project.model.enums;

import java.util.Objects;

/**
 * 
 * @Description: 忽略或者通过 好友请求的枚举
 */
public enum OperatorFriendRequestTypeEnum {
	
	IGNORE(0, "忽略"),
	PASS(1, "通过");
	
	public final Integer type;
	public final String msg;
	
	OperatorFriendRequestTypeEnum(Integer type, String msg){
		this.type = type;
		this.msg = msg;
	}
	
	public Integer getType() {
		return type;
	}

	/***
	 * 查看类型
	 * @param type
	 * @return
	 */
	public static String getMsgByType(Integer type) {
		for (OperatorFriendRequestTypeEnum operType : OperatorFriendRequestTypeEnum.values()) {
			if (Objects.equals(operType.getType(), type)) {
				return operType.msg;
			}
		}
		return null;
	}
	
}
