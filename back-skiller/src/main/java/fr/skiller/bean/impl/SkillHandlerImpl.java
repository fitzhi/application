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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fr.skiller.bean.SkillHandler;
import fr.skiller.data.internal.Skill;
import fr.skiller.service.impl.ApplicationFileSkillsScannerService;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Component("mock.Skill")
public class SkillHandlerImpl implements SkillHandler {

	/**
	 * The skills collection.
	 */
	private HashMap<Integer, Skill> mapSkills;

	/**
	 * Initialization of the Google JSON parser.
	 */
	private static Gson gson = new GsonBuilder().create();

	/**
	 * Resources directory.
	 */
	private static File resourcesDirectory = new File("src/main/resources");

	/**
	 * logger
	 */
	Logger logger = LoggerFactory.getLogger(SkillHandlerImpl.class.getCanonicalName());


	@Override
	public Map<Integer, Skill> getSkills() {
		if (this.mapSkills != null) {
			return this.mapSkills;
		}

		mapSkills = new HashMap<Integer, Skill>();

		FileReader fr;
		try {
			fr = new FileReader(new File(resourcesDirectory.getAbsolutePath() + "/data/skills.json"));
			List<Skill> skillsRead = new ArrayList<Skill>();
			
			Type listSkillType = new TypeToken<ArrayList<Skill>>() {
			}.getType();
			skillsRead = gson.fromJson(fr, listSkillType);

			skillsRead.forEach(skill -> mapSkills.put(skill.id, skill));
			
			if (logger.isDebugEnabled()) {
				logger.debug("Reading " + skillsRead.size() + " skills.");
			}
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
