package com.iauto.wlink.core.auth.provider;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.iauto.wlink.core.auth.TicketAuthentication;

public class TicketAuthenticationProviderTest {

	private static final String key = "UhZr6vyeBu0KmlX9"; // 128 bit key
	private static final String initVector = "UTbKkKQ335whZicI"; // 16 bytes IV

	private TicketAuthenticationProvider authenticationProvider;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		authenticationProvider = new ReserveAccountTicketAuthenticationProvider( key, initVector );
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAuthenticateAuthentication() {
	}

	@Test
	public void testSupports() {
		Assert.assertTrue( authenticationProvider.supports( TicketAuthentication.class ) );
		Assert.assertFalse( authenticationProvider.supports( TicketAuthenticationProviderTest.class ) );
	}

}
