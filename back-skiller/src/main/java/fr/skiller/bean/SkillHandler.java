package fr.skiller.bean;

import java.util.Map;
import java.util.Optional;

import fr.skiller.data.internal.Staff;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Skill;

/**
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public interface SkillHandler {

	Map<Integer, Skill> getSkills();

	/**
	 * Search for a skill associated to the passed name. 
	 * @param skillName 
	 * @return
	 */
	Optional<Skill> lookup(final String skillName);


}
