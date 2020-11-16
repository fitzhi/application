/**
 * 
 */
package com.fitzhi.data;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.fitzhi.data.internal.AuditTopic;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.SkillDetectorType;
import com.fitzhi.data.internal.Staff;
import com.google.gson.Gson;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
public class JsonTest extends TestCase {
	
	@Test
	public void testSimple() {
		Staff collab = new Staff(1, "firtname", "lastName", "nickName", "login","email", "level");
		
		Gson g = new Gson();
		LoggerFactory.getLogger(JsonTest.class).debug(g.toJson(collab)); 
	}

	@Test
	public void testSerializeProject() {
		Project project = new Project(1789, "Revolution");
		
		Gson g = new Gson();
		if (log.isDebugEnabled()) {
			log.debug(g.toJson(project));
		}
		LoggerFactory.getLogger(JsonTest.class).debug(g.toJson(project)); 
	}

	@Test
	public void testSerializationMap() {
		Map<Integer, AuditTopic> audit = new HashMap<>();
		audit.put(1, new AuditTopic(1));
		Gson g = new Gson();
		if (log.isDebugEnabled()) {
			log.debug(g.toJson(audit)); 
		}			
	}

	@Test
	public void testSkillDetectorType() {
		Gson g = new Gson();
		String detectors = g.toJson(
					SkillDetectorType.getDetectorTypes());
		Assert.assertEquals("{\"0\":\"Filename filter pattern\",\"1\":\"Dependency detection in the package.json file\",\"2\":\"Dependency detection in the pom.xml file\"}", detectors);
	}
		
}
