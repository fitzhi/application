package fr.skiller.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fr.skiller.data.internal.Skill;
import junit.framework.TestCase;

public class GenerateSkillsInJSONTest extends TestCase {

	private static File resourcesDirectory = new File("src/test/resources");

	private static Logger logger = LoggerFactory.getLogger(GenerateSkillsInJSONTest.class.getCanonicalName());

	/**
	 * Initialization of the Google JSON parser.
	 */
	private static Gson gson = new GsonBuilder().create();

	@Test
	public void testGenerate() throws IOException {

		List<Skill> skills = new ArrayList<Skill>();
		final BufferedReader br = new BufferedReader(
				new FileReader(new File(resourcesDirectory.getAbsolutePath() + "/opennlp/skills")));
		br.lines().forEach(title -> skills.add(new Skill(skills.size() + 1, title)));
		br.close();

		String s = gson.toJson(skills);
		BufferedWriter bw = new BufferedWriter(
				new FileWriter(new File(resourcesDirectory.getAbsolutePath() + "/opennlp/skills.json")));
		bw.write(s);
		bw.close();

		Assert.assertTrue(true);
	}

	@Test
	public static List<Skill> getReadSkillsJson() throws IOException {

		final FileReader fr = new FileReader(new File(resourcesDirectory.getAbsolutePath() + "/opennlp/skills.json"));

		List<Skill> skills = new ArrayList<>();
		Type listSkillType = new TypeToken<ArrayList<Skill>>() {
		}.getType();
		skills = gson.fromJson(fr, listSkillType);

		skills.stream().forEach(skill -> logger.debug(skill.getTitle()));

		return skills;
	}

}
