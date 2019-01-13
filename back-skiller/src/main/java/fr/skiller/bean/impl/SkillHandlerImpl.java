/**
 * 
 */
package fr.skiller.bean.impl;

import static fr.skiller.Error.CODE_SKILL_NOFOUND;
import static fr.skiller.Error.MESSAGE_SKILL_NOFOUND;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fr.skiller.bean.DataSaver;
import fr.skiller.bean.SkillHandler;
import fr.skiller.data.internal.Skill;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;
import fr.skiller.service.impl.ApplicationFileSkillsScannerService;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Component
public class SkillHandlerImpl extends AbstractDataSaverLifeCycleImpl implements SkillHandler {

	/**
	 * The skills collection.
	 */
	private Map<Integer, Skill> skills;

	/**
	 * Initialization of the Google JSON parser.
	 */
	private static Gson gson = new GsonBuilder().create();

	/**
	 * Data access interface.
	 */
	@Autowired
	DataSaver dataSaver;

	/**
	 * logger
	 */
	Logger logger = LoggerFactory.getLogger(SkillHandlerImpl.class.getCanonicalName());


	@Override
	public Map<Integer, Skill> getSkills() {
		
		if (this.skills != null) {
			return this.skills;
		}

		try {
			this.skills = dataSaver.loadSkills();
			if (logger.isDebugEnabled()) {
				logger.debug(this.skills.size() + " skills loaded");
			}
		} catch (final SkillerException e) {
			// Without skills, this application is absolutely not viable
			throw new RuntimeException(e);
		}
		return this.skills;		
	}

	@Override
	public Optional<Skill> lookup(final String skillTitle) {
		return getSkills().values().stream()
				.filter((Skill skill) -> skill.title.toUpperCase().equals(skillTitle.toUpperCase())).findFirst();
	}

	@Override
	public Skill addNewSkill(final Skill skill) {
		synchronized (lockDataUpdated) {
			Map<Integer, Skill> skills = getSkills();
			skill.id = skills.size() + 1;
			skills.put(skill.id, skill);
			this.dataUpdated = true;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Skill added " + skill.toString());
		}
		return skill;
	}

	@Override
	public boolean containsSkill(final int idSkill) {
		return getSkills().containsKey(idSkill);
	}

	@Override
	public void saveSkill(final Skill skill) throws SkillerException {
		if (skill.id == 0) {
			throw new SkillerException(CODE_SKILL_NOFOUND, MessageFormat.format(MESSAGE_SKILL_NOFOUND, skill.id));
		}
		synchronized (lockDataUpdated) {
			getSkills().put(skill.id, skill);
			this.dataUpdated = true;
		}
	}

}
