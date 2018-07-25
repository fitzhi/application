/**
 * 
 */
package fr.skiller.data;

import java.util.ArrayList;

import org.junit.Test;

import com.google.gson.Gson;

import junit.framework.TestCase;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class JsonTest extends TestCase {
	
	@Test
	public void testSimple() {
		Collabotator collab = new Collabotator(1, "firtname", "lastName", "nickName", "email", "level");
		
		Gson g = new Gson();

		System.out.println(g.toJson(collab)); 
	}

}
