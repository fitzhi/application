/**
 * 
 */
package com.tixhi.bean.impl;

import static com.tixhi.Error.CODE_SKILL_NOFOUND;
import static com.tixhi.Error.MESSAGE_SKILL_NOFOUND;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tixhi.SkillerRuntimeException;
import com.tixhi.bean.DataHandler;
import com.tixhi.bean.SkillHandler;
import com.tixhi.data.internal.Skill;
import com.tixhi.exception.SkillerException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
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
	DataHandler dataSaver;

	@Override
	public Map<Integer, Skill> getSkills() {
		
		if (this.skills != null) {
			return this.skills;
		}

		try {
			this.skills = dataSaver.loadSkills();
			if (log.isDebugEnabled()) {
				log.debug(String.format("%d %s", this.skills.size(), "skills loaded"));
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
			int id = getSkills().size()+1;
			skill.setId(id);
			getSkills().put(id, skill);
			this.dataUpdated = true;
		}
		if (log.isDebugEnabled()) {
			log.debug(String.format("Skill added %s", skill.toString()));
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
