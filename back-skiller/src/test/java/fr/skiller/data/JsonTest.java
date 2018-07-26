/**
 * 
 */
package fr.skiller.data;

import java.util.ArrayList;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ch.qos.logback.classic.Logger;
import junit.framework.TestCase;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class JsonTest extends TestCase {
	
	@Test
	public void testSimple() {
		Collaborator collab = new Collaborator(1, "firtname", "lastName", "nickName", "email", "level");
		
		Gson g = new Gson();
		LoggerFactory.getLogger(JsonTest.class).debug(g.toJson(collab)); 
	}

}
