package com.fitzhi.bean;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fitzhi.bean.impl.SkillHandlerImpl;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SkillDetectorType;
import com.fitzhi.data.source.CommitHistory;
import com.fitzhi.exception.ApplicationException;

/**
 * <p>
 * This interface is a bean interface. Its role is to handle the skills registered in Fitzhì.
 * <br/>
 * The main Bean is {@link SkillHandlerImpl}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
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
	  * @throws ApplicationException exception occurs
	  */
	 void saveSkill(Skill skill) throws ApplicationException;
	
	 /**
	  * <p>Retrieve the skill corresponding to the passed identifier</p>
	  * @param idSkill the search skill identifier.
	  * @return the skill found.
	  * @throws ApplicationException thrown if the passed id does not exist.
	  */
	 Skill getSkill(int idSkill) throws ApplicationException;

	 /**
	  * @return the map containing the detector types.
	  * @throws ApplicationException thrown if any exception occurs. Most probably an IOException.
	  */
	 Map<Integer, String> detectorTypes() throws ApplicationException;
	 
	 /**
	  * Extract the skills detected in the GIT repository of a project.
	  * @param rootPath local path where the project repository has been cloned
	  * @param entries history of {@link CommitHistory commits} aggregated for this repository 
	  * @return a map a {@link ProjectSkill skills} detected in the repository & indexed by skill identifier 
	  * @throws ApplicationException if any exception occurs, <i>most probably an IOException</i>
	  */
	 Map<Integer, ProjectSkill> extractSkills(String rootPath, List<CommitHistory> entries) throws ApplicationException;

	/**
	 * <p>
	 * Test if the passed skill is detected in the filename.
	 * </p>
	 * <p>
	 * <font color="darkRed">
	 * This method does not detect any kind of skills, but only skills detected by their filename. 
	 * If the passed skill has not a {@link SkillDetectorType#FILENAME_DETECTOR_TYPE}, then the method will return {@code false}
	 * </font>
	 * </p>
	 * @param skill the skill candidate to be detected
	 * @param sourcePath the path of a source file 
	 * @return {@code true} if this skill is detected, {@code false} otherwise
	 * @throws ApplicationException exception thrown if any problem occurs (most probably an IOException)
	 */
	boolean isSkillDetectedWithFilename(Skill skill, String sourcePath);

}
