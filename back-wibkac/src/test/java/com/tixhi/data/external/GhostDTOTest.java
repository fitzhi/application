/**
 * 
 */
package com.tixhi.data.external;



import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.tixhi.data.external.Action;
import com.tixhi.data.internal.Committer;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class GhostDTOTest {

	Logger logger = LoggerFactory.getLogger(GhostDTOTest.class.getCanonicalName());

	@Test
	public void testSimple() {
		Committer pseudo = new Committer();
		pseudo.setIdStaff(1);
		pseudo.setPseudo("test");
		pseudo.setLogin("loginTest");
		pseudo.setAction(Action.A);
		Gson g = new Gson();

		Assert.assertTrue("{\"pseudo\":\"test\",\"idStaff\":1,\"login\":\"loginTest\",\"technical\":false,\"action\":\"A\"}".equals(g.toJson(pseudo)));
		if (logger.isDebugEnabled()) {
			logger.debug(g.toJson(pseudo)); 
		}
	}

}
