package com.iauto.wlink.core.auth.provider;

import com.iauto.wlink.core.auth.Authentication;
import com.iauto.wlink.core.auth.AuthenticationProvider;
import com.iauto.wlink.core.auth.TicketAuthentication;
import com.iauto.wlink.core.exception.AuthenticationException;

public abstract class TicketAuthenticationProvider implements AuthenticationProvider {

	public Authentication authenticate( Authentication authentication ) throws AuthenticationException {

		if ( !supports( authentication.getClass() ) )
			return null;

		TicketAuthentication ticketAuth = (TicketAuthentication) authentication;

		return authenticate( (String) ticketAuth.credential() );
	}

	abstract TicketAuthentication authenticate( String ticket ) throws AuthenticationException;

	public boolean supports( Class<?> authentication ) {
		return ( TicketAuthentication.class.isAssignableFrom( authentication ) );
	}
}
