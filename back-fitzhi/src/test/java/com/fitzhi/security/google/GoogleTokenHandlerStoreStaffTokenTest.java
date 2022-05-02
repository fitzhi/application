package com.fitzhi.security.google;

import static com.fitzhi.Global.GITHUB_OPENID_SERVER;
import static com.fitzhi.Global.GOOGLE_OPENID_SERVER;

import java.util.ArrayList;
import java.util.List;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.data.internal.OpenId;
import com.fitzhi.data.internal.OpenIdToken;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.security.token.TokenHandler;
import com.fitzhi.security.token.google.GoogleTokenHandlerImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Tests on the class {@link GoogleTokenHandlerImpl#storeStaffToken(com.fitzhi.data.internal.Staff, com.fitzhi.data.internal.OpenIdToken) }
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GoogleTokenHandlerStoreStaffTokenTest {
	
	@Autowired
	@Qualifier("GOOGLE")
	TokenHandler tokenHandler;

	/**
	 * A staff without any external authentificaion declared (Github, Google...)
	 * @throws ApplicationException
	 */
	@Test (expected = ApplicationRuntimeException.class)
	public void noExternalAuthDeclared() throws ApplicationException {
		final Staff staff = new Staff(1789, "firstName", "lastName", "nickName", "login", "email", "level");
		OpenIdToken openIdToken = OpenIdToken.of();

		tokenHandler.storeStaffToken(staff, openIdToken);
	}

	/**
	 * A staff without any external authentificaion declared (Github, Google...)
	 * @throws ApplicationException
	 */
	@Test (expected = ApplicationRuntimeException.class)
	public void NoGoogleAuthDeclated() throws ApplicationException {
		final Staff staff = new Staff(1789, "firstName", "lastName", "nickName", "login", "email", "level");

		OpenId openId = OpenId.of(GITHUB_OPENID_SERVER, "userId");
		List<OpenId> list = new ArrayList<>();
		list.add(openId);
		staff.setOpenIds(list);

		OpenIdToken openIdToken = OpenIdToken.of();
		tokenHandler.storeStaffToken(staff, openIdToken);
	}

	/**
	 * A staff without any external authentificaion declared (Github, Google...)
	 * @throws ApplicationException
	 */
	@Test (expected = ApplicationException.class)
	public void googleAuthDeclated() throws ApplicationException {
		final Staff staff = new Staff(1789, "firstName", "lastName", "nickName", "login", "email", "level");

		OpenId openId = OpenId.of(GOOGLE_OPENID_SERVER, "userId");
		List<OpenId> list = new ArrayList<>();
		list.add(openId);
		staff.setOpenIds(list);

		OpenIdToken openIdToken = OpenIdToken.of();
		tokenHandler.storeStaffToken(staff, openIdToken);
	}


}
