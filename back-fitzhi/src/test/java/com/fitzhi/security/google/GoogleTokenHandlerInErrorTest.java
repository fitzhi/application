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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Tests on the class {@link GoogleTokenHandlerImpl }
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "referential.dir=nonExistingPath" }) 
public class GoogleTokenHandlerInErrorTest {
	
	@Autowired
	@Qualifier("GOOGLE")
	TokenHandler tokenHandler;

	/**
	 * Initialize IN ERROR the Google Token Handler.
	 * The resulting ApplicationException will be raised will be invoking a method from the TokenHandler.
	 * 
	 * @throws ApplicationException
	 */
	@Test (expected = ApplicationException.class)
	public void initInErrorTakeInAccountToken() throws ApplicationException {
		tokenHandler.takeInAccountToken("idTokenString");
	}

	/**
	 * Initialize IN ERROR the Google Token Handler.
	 * The TokenHandler will be considered as not declared.
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void initInErrorIsDeclared(){
		Assert.assertFalse(tokenHandler.isDeclared());
	}

}


