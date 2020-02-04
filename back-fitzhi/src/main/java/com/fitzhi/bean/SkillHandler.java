package com.fitzhi.bean;

import java.util.Map;
import java.util.Optional;

import com.fitzhi.data.internal.Skill;
import com.fitzhi.exception.SkillerException;

/**
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public interface SkillHandler extends DataSaverLifeCycle {

	Map<Integer, Skill> getSkills();

	/**
	 * Search for a skill associated to the passed name. 
	 * @param skillName 
	 * @return
	 */
	Optional<Skill> lookup(final String skillName);

	 /**
	  * @param skill the new skill to add
	  * @return the newly skill created
	  */
	 Skill addNewSkill(Skill skill) ;
	 
	 /**
	  * @param idSkill the passed skill identifier
	  * @return {@code true} if a skill responds to this id, {@code false} otherwise
	  */
	 boolean containsSkill(int idSkill);

	 /**
	  * @param skill the new skill
	  * @throws SkillerException exception occurs
	  */
	 void saveSkill(Skill skill) throws SkillerException;
	
	 /**
	  * <p>Retrieve the skill corresponding to the passed identifier</p>
	  * @param idSkill the search skill identifier.
	  * @return the skill found.
	  * @throws SkillerException thrown if the passed id does not exist.
	  */
	 Skill getSkill(int idSkill) throws SkillerException;

}
