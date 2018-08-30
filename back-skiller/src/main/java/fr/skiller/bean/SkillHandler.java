package fr.skiller.bean;

import java.util.Map;

import fr.skiller.data.internal.Collaborator;
import fr.skiller.data.internal.Skill;

/**
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public interface SkillHandler {

	Map<Integer, Skill> getSkills();

}
