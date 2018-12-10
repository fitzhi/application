/**
 * 
 */
package fr.skiller.data;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import fr.skiller.data.internal.Staff;
import junit.framework.TestCase;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class JsonTest extends TestCase {
	
	@Test
	public void testSimple() {
		Staff collab = new Staff(1, "firtname", "lastName", "nickName", "login","email", "level");
		
		Gson g = new Gson();
		LoggerFactory.getLogger(JsonTest.class).debug(g.toJson(collab)); 
	}

}
