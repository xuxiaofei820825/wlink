package com.iauto.wlink.core.session;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.iauto.wlink.core.exception.InvalidSignatureSessionException;
import com.iauto.wlink.core.exception.MessageProcessException;

public class HMacSessionSignHandlerTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	private String id = "149e503cc7944583813fdc8e8d9f94e6";
	private String tuid = "U000002";
	private long expiredTime = 1484208808572L;
	private String signature = "RP6Tc8UUfbpdgFRHDfHTeda7SgQe0XXhu3b6kK-KnLo";

	/** 密匙 */
	private String KEY = "9aROHg2eQXQ6X3XKrXGKWjXrLiRIO25CKTyz212ujvc";

	private HMacSessionSignHandler sessionSignHandler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		sessionSignHandler = new HMacSessionSignHandler( KEY );
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected = InvalidSignatureSessionException.class)
	public void testValidateThrowsInvalidSignatureSessionException() throws MessageProcessException {
		sessionSignHandler.validate( id, tuid, expiredTime, "AAAAAA" );
	}

	@Test
	public void testSign() {

		String rs = sessionSignHandler.sign( id, tuid, expiredTime );

		// 验证结果
		Assert.assertEquals( signature, rs );
	}
}
