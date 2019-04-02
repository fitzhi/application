/**
 * 
 */
package fr.skiller.data.external;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import fr.skiller.data.internal.Pseudo;
import org.junit.Assert;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class GhostDTOTest {

	@Test
	public void testSimple() {
		Pseudo pseudo = new Pseudo();
		pseudo.setIdStaff(1);
		pseudo.setCommitPseudo("test");
		pseudo.setLogin("loginTest");
		pseudo.setAction(Action.A);
		Gson g = new Gson();

		Assert.assertTrue("{\"commitPseudo\":\"test\",\"idStaff\":1,\"login\":\"loginTest\",\"technical\":false,\"action\":\"A\"}".equals(g.toJson(pseudo)));
		LoggerFactory.getLogger(GhostDTOTest.class).debug(g.toJson(pseudo)); 
	}

}
