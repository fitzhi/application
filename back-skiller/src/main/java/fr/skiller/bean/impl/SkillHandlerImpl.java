/**
 * 
 */
package fr.skiller.bean.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import fr.skiller.bean.SkillHandler;
import fr.skiller.data.internal.Project;
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
	private HashMap<Integer, Skill> skill;

	@Override
	public Map<Integer, Skill> getSkills() {
		if (this.skill != null) {
			return this.skill;
		}
		this.skill = new HashMap<Integer, Skill>();
		this.skill.put(1, new Skill(1, "Java"));
		this.skill.put(2, new Skill(2, "Spring"));
		this.skill.put(3, new Skill(3, "Spring Framework"));
		this.skill.put(4, new Skill(4, "Spring Boot"));
		this.skill.put(5, new Skill(5, "hibernate"));
		this.skill.put(6, new Skill(5, ".Net"));
		return skill;
	}
	
	@Override
	public Optional<Skill> lookup(final String skillTitle) {
		return getSkills().values().stream()
				.filter( (Skill skill) -> skill.title.toUpperCase().equals(skillTitle.toUpperCase()))
				.findFirst();
	}

}
