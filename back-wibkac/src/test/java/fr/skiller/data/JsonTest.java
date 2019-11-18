/**
 * 
 */
package fr.skiller.data;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import fr.skiller.data.internal.AuditTopic;
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

	
	public void testSerializationMap() {
		Map<Integer, AuditTopic> audit = new HashMap<>();
		audit.put(1, new AuditTopic(1));
		Gson g = new Gson();
		LoggerFactory.getLogger(JsonTest.class).debug(g.toJson(audit)); 
			
	}
}
