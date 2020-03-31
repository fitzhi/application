package com.fitzhi.bean;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fitzhi.bean.impl.StringTransform;
import com.fitzhi.data.internal.Experience;
import com.fitzhi.data.internal.PeopleCountExperienceMap;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ResumeSkill;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.exception.SkillerException;

/**
 * Interface in charge of handling the staff collection.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface StaffHandler extends DataSaverLifeCycle {

	/**
	 * <p>Initialize the content of the in-memory staffs.</p>
	 * <i>This method exists only for testing purpose</i>
	 */
	 void init();
	
	 /**
	  * @return the staff list.
	  */
	Map<Integer, Staff> getStaff();

	/**
	 * Add a new technical employee inside the company.
	 * @param isStaff : identified of this new employee
	 * @param staff : Staff instance representing this employee
	 * @return the previous staff member associated with this idStaff, or null if there was no mapping for this id. 
	 */
	Staff put (final int idStaff, final Staff staff);
	
	 /**
	  * @param active <code>true</code> Only active developers are taking account, <code>false</code> all developers are included. 
	  * @return the number of developers group by skills registered into a Map.
	  */
	PeopleCountExperienceMap countAllStaffGroupBySkillLevel(boolean active);
	
	/**
	 * Add a collection of skills usually extracted from the employe's resume.
	 * @param idStaff the identifier for this staff member
	 * @param skills the skills detected
	 * @return the updated Staff
	 * @throws SkillerException Thrown if any error occurs during the treatment
	 */
	Staff addExperiences (int idStaff, ResumeSkill[] skills) throws SkillerException ;
	
	/**
	 * Lookup for staff members responding to a polymorphous criteria.<br/>
	 * For this release, 2 scenarios are implemented regarding the content of this criteria : <br/>
	 * <ul>
	 * <li>The criteria contains ONE word and therefore, it's corresponding either to the connection login, or the last name, or the first name <i>(in that order)</i>.</li> 
	 * <li>The criteria contains MULTIPLE words and therefore, it's corresponding to user full name (first + last) or (last+first).</li> 
	 * </ul>
	 * @param criteria polymorphous criteria
	 * @return the <i>first</i> staff corresponding to the criteria, or NULL is none's found
	 */
	Staff lookup(String criteria);
	
	/**
	 * @param idStaff the passed staff's identifier
	 * @return {@code true} if this person is still active in the staff<br/> {@code false} otherwise 
	 */
	boolean isActive (int idStaff) ;
	
	/**
	 * <p>
	 * Involve the contributors into the project.<br/>
	 * The method add, or update, the missions for every staff member present in the contributors list.
	 * </p>
	 * @param project the current project
	 * @param contributors the list of contributors retrieved from the repository
	 * @throws SkillerException thrown if any problem occurs 
	 */
	void involve(Project project, List<Contributor> contributors) throws SkillerException;
	
	/**
	 * <p>
	 * Involve a single contributors into the project.<br/>
	 * The method add, or update, the missions for the staff associated with the contributor.
	 * </p>
	 * @param project the current project
	 * @param contributor the contributor data representing the contribution of a staff member in a project.
	 * @throws SkillerException thrown if any problem occurs 
	 */
	void involve(Project project, Contributor contributor) throws SkillerException;
	
	/**
	 * <p>
	 * Retrieve the contributors list for the given project.
	 * </p>
	 * @param project the project identifier.
	 * @return the list of contributors, which might be empty (but not <code>null</code>)
	 */
	List<Contributor> getContributors(int projectId);
	
	/**
	 * @param idStaff the staff identifier
	 * @return the full name of the staff member found, or {@code null} is none's found
	 */
	String getFullname (int idStaff);

	 /**
	  * @param staff the new staff member to add
	  * @return the newly created staff member
	  */
	 Staff addNewStaffMember(Staff staff) ;
	 
	 /**
	  * @param ifStaff the passed staff identifier
	  * @return {@code true} if a project exists for this project identifier, {@code false} otherwise
	  */
	 boolean containsStaffMember(int idStaff);

	 /**
	  * <p>Control and save the given staff.</p>
	  * @param staff the staff member to be updated.
	  * @throws SkillerException thrown if an exception occurs such as for instance
	  * <ul>
	  * <li>This given staff does not yet exist (idStaff=0)</li>
	  * <li>The same login already in the workforce.</li>
	  * </ul>
	  */
	 void saveStaffMember(Staff staff) throws SkillerException;

	 /**
	  * <p>Search a staff, if any, who has his login equal to the passed one.</p>
	  * <p><b>Reminder : </b><i>The login property is also the connection login for the application. It has to be unique.</i></p>
	  * @param login the given login
	  * @return an optional object containing 
	  * <ul>
	  * <li>either the staff corresponding to the given login,</li>
	  * <li>or <code>null</code> is none exists</li>
	  * </ul>
	  */
	 Optional<Staff> findStaffWithLogin(String login);

	 /**
	  * <p>Add the experience to a staff.</p>
	  * @param idStaff the staff identifier
	  * @param experience the experience to be added
	  */
	 void addExperience(int idStaff, Experience experience);
	 
	 /**
	  * <p>Remove the experience from the staff.</p>
	  * @param idStaff the staff identifier
	  * @param experience the experience to be added
	  */
	 void removeExperience(int idStaff, Experience experience);

	 /**
	  * <p>Update THE LEVEL ONLY of an experience.</p>
	  * @param idStaff the staff identifier
	  * @param experience the experience whose level has to be updated.
	  */
	 void updateExperience(int idStaff, Experience experience);
	 
	 /**
	  * <p>
	  * Add a project inside the missions of a staff member.
	  * </p>
	  * @param idStaff the staff identifier
	  * @param idProject the project identifier
	  * @param projectName the name of the project
	  */	 
	 void addMission(int idStaff, int idProject, String projectName);
	 
	 /**
	  * <p>
	  * Revoke a project from the missions of a staff member.
	  * </p>
	  * @param idStaff the staff identifier
	  * @param idProject the project identifier
	  */	 
	 void delMission(int idStaff, int idProject);
	 
	 /**
	  * <p>
	  * Get and return a staff member given his identifier.
	  * </p>
	  * @param idStaff the staff identifier.
	  * @return the selected staff or {@code null} if none exists.
	  */
	 Staff getStaff(int idStaff);
	 
	 /**
	  * <p>
	  * Test the eligibility of a staff member to meet a specific criteria. 
	  * </p>
	  * @param staff the candidate staff
	  * @param pattern the given pattern to match. <i>The pattern is in the current release actually </i>
	  * @return {@code true} if the given staff is matching the passed pattern, {@code false} otherwise. 
	  */
	 public boolean isEligible(Staff staff, String pattern);
		
	 /**
	  * <p>
	  * Test the eligibility of a staff member to meet a specific criteria. 
	  * </p>
	  * @param staff the candidate staff
	  * @param pattern the given pattern to match
	  * @param transformer the string transformer
	  * @return {@code true} if the candidate is matching the given pattern, {@code false} otherwise. 
	  */
	 public boolean isEligible(Staff staff, String pattern, StringTransform transformer);
	
	 /**
	  * Save the <b>encrypted</b> password 
	  * @param staff the staff whose password has be saved
	  * @param password the encrypted password
	  */
	 public void savePassword(Staff staff, String password);
	 
	 /**
	  * Infer the skills for a developer based on his mission and their metrics.
	  * @param idStaff the staff identifier
	  * @throws SkillerException
	  */
	 void inferSkillsFromMissions(int idStaff) throws SkillerException;
	 
	 /**
	  * Update the activity state.
	  * @param staff the current staff whose status must be set. 
	  */
	 void updateActiveState(Staff staff);
}

