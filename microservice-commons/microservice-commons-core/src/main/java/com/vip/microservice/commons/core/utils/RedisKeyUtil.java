package com.vip.microservice.commons.core.utils;

import com.google.common.base.Preconditions;
import com.vip.microservice.commons.core.support.Objects;
import lombok.experimental.UtilityClass;

import java.text.MessageFormat;

@UtilityClass
public class RedisKeyUtil {

	private static final String OAUTH2_LOGIN_ERROR_TIMES = "admin:oauth2:login-error-times:{0}:{1}";
	private static final String OAUTH2_VERIFY_ERROR_TIMES = "admin:oauth2:verify-error-times:{0}:{1}";

	/**
	 * Gets login error times
	 *
	 * @param client the client
	 *
	 * @return the send sms rate key
	 */
	public static String getOauth2LoginErrorTimes(String client, String username) {
		Preconditions.checkArgument(Objects.isNotEmpty(client), "客户端不能为空");
		Preconditions.checkArgument(Objects.isNotEmpty(username), "用户名不能为空");
		return MessageFormat.format(OAUTH2_LOGIN_ERROR_TIMES, client, username);
	}

	/**
	 * Gets verify error times
	 *
	 * @param client the client
	 *
	 * @return the send sms rate key
	 */
	public static String getOauth2VerifyErrorTimes(String client, String username) {
		Preconditions.checkArgument(Objects.isNotEmpty(client), "客户端不能为空");
		Preconditions.checkArgument(Objects.isNotEmpty(username), "用户名不能为空");
		return MessageFormat.format(OAUTH2_VERIFY_ERROR_TIMES, client, username);
	}

}
