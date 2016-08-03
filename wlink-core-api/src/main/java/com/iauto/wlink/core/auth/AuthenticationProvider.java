package com.iauto.wlink.core.auth;

import com.iauto.wlink.core.exception.AuthenticationException;

public interface AuthenticationProvider {
	long authenticate( String ticket ) throws AuthenticationException;
}
