package com.iauto.wlink.core.auth.service;

import com.iauto.wlink.core.exception.AuthenticationException;

public interface AuthenticationProvider {
	String authenticate( String ticket ) throws AuthenticationException;
}
