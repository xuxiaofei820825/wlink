package com.iauto.wlink.core.auth.service;

import com.iauto.wlink.core.exception.AuthenticationException;

public class MutipleAuthenticationProvider implements AuthenticationProvider {

	public long authenticate( String ticket ) throws AuthenticationException {
		return -1;
	}
}
