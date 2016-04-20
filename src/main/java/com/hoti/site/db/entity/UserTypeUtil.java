package com.hoti.site.db.entity;

import java.util.Arrays;

/**
 * 用户类型工具
 * @author larry.qi
 */
public class UserTypeUtil {
	
	/**
	 * 用户类型
	 * <li>1=普通用户</li>
	 * <li>2=管理员</li>
	 */
	private final static Byte[] USER_TYPES = {1, 2};
	
	/**
	 * 验证用户类型
	 * @param userType
	 * @return
	 */
	public static boolean validate(Byte type) {
		return Arrays.asList(USER_TYPES).contains(type);
	}

}