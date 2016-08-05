package com.iauto.wlink.core.auth;

import com.iauto.wlink.core.exception.AuthenticationException;

public interface AuthenticationProvider {
	Authentication authenticate( Authentication authentication ) throws AuthenticationException;
	boolean supports( Class<?> authentication ) ;
}
