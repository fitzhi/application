/**
 * 
 */
package fr.skiller.bean.impl;

import static fr.skiller.Error.CODE_SKILL_NOFOUND;
import static fr.skiller.Error.MESSAGE_SKILL_NOFOUND;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.skiller.SkillerRuntimeException;
import fr.skiller.bean.DataSaver;
import fr.skiller.bean.SkillHandler;
import fr.skiller.data.internal.Skill;
import fr.skiller.exception.SkillerException;

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
				logger.debug(String.format("%d %s", this.skills.size(), "skills loaded"));
			}
		} catch (final SkillerException e) {
			// Without skills, this application is absolutely not viable
			throw new SkillerRuntimeException(e);
		}
		return this.skills;		
	}

	@Override
	public Optional<Skill> lookup(final String skillTitle) {
		return getSkills().values().stream()
				.filter((Skill skill) -> skill.getTitle().equalsIgnoreCase(skillTitle.toUpperCase())).findFirst();
	}

	@Override
	public Skill addNewSkill(final Skill skill) {
		synchronized (lockDataUpdated) {
			getSkills().put(getSkills().size()+1, skill);
			this.dataUpdated = true;
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Skill added %s", skill.toString()));
		}
		return skill;
	}

	@Override
	public boolean containsSkill(final int idSkill) {
		return getSkills().containsKey(idSkill);
	}

	@Override
	public void saveSkill(final Skill skill) throws SkillerException {
		if (skill.getId() == 0) {
			throw new SkillerException(CODE_SKILL_NOFOUND, MessageFormat.format(MESSAGE_SKILL_NOFOUND, skill.getId()));
		}
		synchronized (lockDataUpdated) {
			getSkills().put(skill.getId(), skill);
			this.dataUpdated = true;
		}
	}

	@Override
	public Skill getSkill(int idSkill) throws SkillerException {
		
		Skill skill = getSkills().get(idSkill);
		if (skill == null) {
			throw new SkillerException(CODE_SKILL_NOFOUND, MessageFormat.format(MESSAGE_SKILL_NOFOUND, idSkill));
		}
		
		return skill;
	}

}
