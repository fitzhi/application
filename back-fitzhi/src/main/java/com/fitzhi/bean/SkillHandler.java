package com.fitzhi.bean;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.fitzhi.bean.impl.SkillHandlerImpl;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SkillDetectorType;
import com.fitzhi.data.source.CommitHistory;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.NotFoundException;

/**
 * <p>
 * This interface is a bean interface. Its role is to handle the skills registered in Fitzhì.
 * </p>
 * <p>
 * The default bean implementation is {@link SkillHandlerImpl}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface SkillHandler extends DataSaverLifeCycle {

	/**
	 * @return the complete collection of skills declared in the application.
	 */
	Map<Integer, Skill> getSkills();

	/**
	 * Search for a skill associated to the passed name. 
	 * @param skillName 
	 * @return an optional object containing the corresponding skill if any.
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
	  * @param skill the skill object to update.
	  * @throws ApplicationException thrown if any problem occurs during the operation. Most probably this might be an {@link IOException}
	  */
	 void saveSkill(Skill skill) throws ApplicationException;
	
	 /**
	  * <p>
	  * Retrieve the skill corresponding to the passed identifier.
	  * </p>
	  * @param idSkill the SKILL identifier.
	  * @return the skill retrieved in the internal collection.
	  * @throws NotFoundException thrown if the given SKILL identifier does not exist.
	  */
	 @NotNull Skill getSkill(int idSkill) throws NotFoundException;

 	/**
	 * <p>
	 * Retrieve the skill corresponding to the passed identifier.
	 * </p>
	 * 
	 * @param idSkill the SKILL identifier.
	 * @return the skill retrieved in the internal collection, or {@code null} if none exists.
	 */
	Skill lookup(int idSkill);

	 /**
	  * Load and return all types of detectors.
	  * A skill can be detected in the repository by multiple ways. E.g. it might be a :
	  * <ul>
	  * <li>a 'Filename filter pattern',</li>
	  * <li>a 'Dependency detection in the package.json file',</li>
	  * <li>or a 'Dependency detection in the pom.xml file',</li>
	  * <li>...</li>
	  * </ul>
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


	/**
	 * Check if the given source pathname verifies the given pattern.
	 * @param filenameDependencies Marker filename for dependencies (it might be {@code package.json}, or {@code pom.xml})
	 * @param rootPath the rootPath where the repository has been cloned
	 * @param sourcePath the source pathname
	 * @param dependency the pattern to be verified
	 * @return {@code true} if this skill is detected, {@code false} otherwise
	 * @throws ApplicationException exception thrown if any problem occurs (most probably an IOException)
	 */
	boolean checkFilePattern(String filenameDependencies, String rootPath, String sourcePath, String dependency) throws ApplicationException;

}
