package com.fitzhi.security.google;

import com.fitzhi.exception.ApplicationException;
import com.fitzhi.security.token.TokenHandler;
import com.fitzhi.security.token.google.GoogleTokenHandlerImpl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Tests on the class {@link GoogleTokenHandlerImpl }
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GoogleTokenHandlerTest {
	
	@Autowired
	@Qualifier("GOOGLE")
	TokenHandler tokenHandler;

	/**
	 * Initialize the Google Token Handler.
	 * @throws ApplicationException
	 */
	@Test
	public void initNominal() throws ApplicationException {
		Assert.assertTrue(tokenHandler.isDeclared());
		Assert.assertNotNull(tokenHandler.getClientId());
	}
}
