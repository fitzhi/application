/**
 * 
 */
package fr.skiller.bean.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fr.skiller.bean.SkillHandler;
import fr.skiller.data.internal.Skill;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Component("mock.Skill")
public class SkillHandlerImpl implements SkillHandler {

	/**
	 * The skills collection.
	 */
	private HashMap<Integer, Skill> skills;

	/**
	 * Initialization of the Google JSON parser.
	 */
	private static Gson gson = new GsonBuilder().create();

	private static File resourcesDirectory = new File("src/main/resources");

	@Override
	public Map<Integer, Skill> getSkills() {
		if (this.skills != null) {
			return this.skills;
		}

		Map<Integer, Skill> mapSkills = new HashMap<Integer, Skill>();

		FileReader fr;
		try {
			fr = new FileReader(new File(resourcesDirectory.getAbsolutePath() + "/data/skills.json"));
			List<Skill> skillsRead = new ArrayList<Skill>();
			
			Type listSkillType = new TypeToken<ArrayList<Skill>>() {
			}.getType();
			skillsRead = gson.fromJson(fr, listSkillType);

			skillsRead.forEach(skill -> mapSkills.put(skill.id, skill));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mapSkills;
	}

	@Override
	public Optional<Skill> lookup(final String skillTitle) {
		return getSkills().values().stream()
				.filter((Skill skill) -> skill.title.toUpperCase().equals(skillTitle.toUpperCase())).findFirst();
	}

}
