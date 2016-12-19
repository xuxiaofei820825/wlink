package com.iauto.wlink.core.auth;

import com.iauto.wlink.core.exception.AuthenticationException;

/**
 * 抽象认证处理器
 * 
 * @author xiaofei.xu
 * 
 */
public interface AuthenticationProvider {

	/**
	 * 进行认证
	 * 
	 * @param authentication
	 *          需要认证的对象
	 * @return 认证结果
	 * @throws AuthenticationException
	 *           认证不成功，则抛出异常
	 */
	Authentication authenticate( Authentication authentication ) throws AuthenticationException;

	/**
	 * 判定当前处理器是否支持对当前对象的认证
	 * 
	 * @param authentication
	 *          需要认证的对象
	 * @return true:支持，false:不支持
	 */
	boolean supports( Class<?> authentication );
}
