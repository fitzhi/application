package com.fitzhi.bean;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.impl.StringTransform;
import com.fitzhi.controller.StaffController;
import com.fitzhi.data.internal.Author;
import com.fitzhi.data.internal.Constellation;
import com.fitzhi.data.internal.Experience;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.OpenId;
import com.fitzhi.data.internal.OpenIdToken;
import com.fitzhi.data.internal.PeopleCountExperienceMap;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ResumeSkill;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.NotFoundException;

/**
 * Interface in charge of handling the staff collection.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface StaffHandler extends DataSaverLifeCycle {

	/**
	 * <p>
	 * Initialize the content of the in-memory staffs.
	 * </p>
	 * <i>This method exists only for testing purpose</i>
	 */
	void init();

	/**
	 * @return the staff list.
	 */
	Map<Integer, Staff> getStaff();

	/**
	 * Add a new technical employee inside the company.
	 * 
	 * @param idStaff the staff identifier of this new employee
	 * @param staff the Staff instance representing this employee
	 * @return the previous staff member associated with this idStaff, or null if
	 *         there was no mapping for this id.
	 */
	Staff put(final int idStaff, final Staff staff);

	/**
	 * Remove a staff member from the personal.
	 * 
	 * @param idStaff : identifier of the employee to be deleted.
	 * @return the staff member just deleted, or {@code null} if none exists.
	 */
	Staff removeStaff(final int idStaff);

	/**
	 * Remove all references of a project from the staff declared missions.
	 * 
	 * @param idProject : identifier of the project
	 */
	void removeProject(final int idProject);

	/**
	 * @param active <code>true</code> Only active developers are taking account,
	 *               <code>false</code> all developers are included.
	 * @return the number of developers group by skills registered into a Map.
	 */
	PeopleCountExperienceMap countAllStaffGroupBySkillLevel(boolean active);

	/**
	 * Add a collection of skills usually extracted from the employe's resume.
	 * 
	 * @param idStaff the identifier for this staff member
	 * @param skills  the skills detected
	 * @return the updated Staff
	 * @throws ApplicationException Thrown if any error occurs during the treatment
	 */
	Staff addExperiences(int idStaff, ResumeSkill[] skills) throws ApplicationException;

	/**
	 * <p>
	 * Lookup for staff members responding to a polymorphous criteria.<br/>
	 * For this release, 2 scenarios are implemented regarding the content of this
	 * criteria : <br/>
	 * </p>
	 * 
	 * <ul>
	 * <li>The author email has a corresponding staff member with the same
	 * email.</li>
	 * <li>The author name contains ONE word and therefore this name is
	 * corresponding either to the connection login, or the last name, or the first
	 * name <i>(in that order)</i>.</li>
	 * <li>
	 * The author name has MULTIPLE words and therefore his name is assumed to be 
	 * to user fullname (first + last), or (last + first).
	 * </li>
	 * </ul>
	 *
	 * <br/>
	 * @param author polymorphous author used to search for a staff member
	 * @return the <i>first</i> staff corresponding to the criteria, or {@code NULL} if none's found
	 */
	Staff lookup(Author author);

 	/**
	 * <p>
	 * Retrieve the staff member corresponding to the given identifier, if any.
	 * </p>
	 * 
	 * @param idStaff the staff identifier.
	 * @return the selected staff or {@code null} if none exists.
	 */
	Staff lookup(int idStaff);

 	/**
	 * <p>
	 * Retrieve ths staff member, if any, linked with the given credentials.
	 * </p>
	 * 
	 * @param openId the given openId
	 * @return the selected staff or {@code null} if none exists.
	 * @throws ApplicationException thrown if any problem occurs
	 */
	Staff lookup(OpenId openId) throws ApplicationException;

 	/**
	 * <p>
	 * Retrieve the staff member corresponding to the given identifier, if any.
	 * </p>
	 * <p>
	 * if this staff member does not exist (any more), then the exception {@link NotFoundException} is thrown.
	 * </p>
	 * 
	 * @param idStaff the staff identifier.
	 * @return the selected staff
	 * @throws NotFoundException thrown if the ID does not exist
	 */
	@NotNull Staff getStaff(int idStaff) throws NotFoundException;

	/**
	 * Search for a Staff member with the same email address, as the given one
	 * 
	 * @param email the given email address
	 * @return the found staff member or {@code null} if none exists.
	 */
	Staff findStaffOnEmail(String email) throws ApplicationException;

	/**
	 * @param idStaff the passed staff's identifier
	 * @return {@code true} if this person is still active in the staff<br/>
	 *         {@code false} otherwise
	 */
	boolean isActive(int idStaff);

	/**
	 * <p>
	 * Involve the contributors into the project.<br/>
	 * The method add, or update, the missions for every staff member present in the
	 * contributors list.
	 * </p>
	 * 
	 * @param project      the current project
	 * @param contributors the list of contributors retrieved from the repository
	 * @throws ApplicationException thrown if any problem occurs
	 */
	void involve(Project project, List<Contributor> contributors) throws ApplicationException;

	/**
	 * <p>
	 * Involve a single contributor into the project.<br/>
	 * The method add, or update, the missions for the staff associated with the
	 * contributor.
	 * </p>
	 * 
	 * @param project the given project
	 * @param contributor the contributor data representing the contribution of a staff member in a project.
	 */
	void involve(Project project, Contributor contributor);

	/**
	 * <p>
	 * Retrieve the contributors list for the given project.
	 * </p>
	 * 
	 * @param idProject the project identifier.
	 * @return the list of contributors, which might be empty (but not
	 *         <code>null</code>)
	 */
	List<Contributor> getContributors(int idProject);

	/**
	 * @param idStaff the staff identifier
	 * @return the full name of the staff member found, or {@code null} is none's
	 *         found
	 */
	String getFullname(int idStaff);

	/**
	 * @param idStaff the passed staff identifier
	 * @return {@code true} if a project exists for this project identifier,
	 *         {@code false} otherwise
	 */
	boolean containsStaffMember(int idStaff);

	/**
	 * <p>
	 * Validate the given staff member.
	 * </p>
	 * Validation rules might be :
	 * <ul>
	 * <li>This given staff does not yet exist <em>for the update
	 * operation</em></li>
	 * <li>The same {@code login} already exists in the workforce.</li>
	 * </ul>
	 * 
	 * @param staff the member of workforce to control before creation, or
	 *              modification
	 * @throws ApplicationException thrown if the validation fails.
	 */
	void controlWorkforceMember(Staff staff) throws ApplicationException;

	/**
	 * <p>
	 * Add the given staff member to the workforce.
	 * </p>
	 * 
	 * @param staff the new staff member to be added
	 * @return the newly created staff member
	 * @throws ApplicationException thrown if any problems occurs during the
	 *                              validation of this staff
	 * @see #controlWorkforceMember(Staff)
	 */
	Staff createWorkforceMember(Staff staff) throws ApplicationException;

	/**
	 * <p>
	 * Create an almost empty staff member based on the commit information
	 * </p>
	 * 
	 * @param author the author of the commit
	 * @return the newly created staff member
	 * @throws ApplicationException thrown if any problems occurs during the
	 *                              validation of this staff
	 * @see #controlWorkforceMember(Staff)
	 */
	Staff createEmptyStaff(Author author) throws ApplicationException;

	/**
	 * Create a staff member based an OpenIdToken.
	 * @param openIdToken the given openIdToken loaded from the authentication server
	 * @return the newly created staff member
	 * @throws ApplicationException thrown if any problem occurs during the validation of this staff member
	 */
	Staff createStaffMember(OpenIdToken openIdToken) throws ApplicationException;

	/**
	 * <p>
	 * Control and save the given staff.
	 * </p>
	 * 
	 * @param staff the staff member to be updated.
	 * @throws ApplicationException thrown if any problems occurs during the
	 *                              validation of this staff
	 * @see #controlWorkforceMember(Staff)
	 */
	void updateWorkforceMember(Staff staff) throws ApplicationException;

	/**
	 * <p>
	 * Search a staff, if any, who has his login equal to the passed one.
	 * </p>
	 * <p>
	 * <b>Reminder : </b><i>The login property is also the connection login for the
	 * application. It has to be unique.</i>
	 * </p>
	 * 
	 * @param login the given login
	 * @return an optional object containing
	 *         <ul>
	 *         <li>either the staff corresponding to the given login,</li>
	 *         <li>or <code>null</code> is none exists</li>
	 *         </ul>
	 */
	Optional<Staff> findStaffOnLogin(String login);

	/**
	 * <p>
	 * Add the experience to a staff.
	 * </p>
	 * 
	 * @param idStaff    the staff identifier
	 * @param experience the experience to be added
	 */
	void addExperience(int idStaff, Experience experience);

	/**
	 * <p>
	 * Remove the experience from the staff.
	 * </p>
	 * 
	 * @param idStaff    the staff identifier
	 * @param experience the experience to be added
	 */
	void removeExperience(int idStaff, Experience experience);

	/**
	 * <p>
	 * Update ONLY THE LEVEL of an existing experience.
	 * </p>
	 * <p>
	 * If there is no staff member for the given {@code idStaff}, this method will throw a {@link ApplicationRuntimeException}.
	 * <b>The {@code idStaff} must be valid.</b>
	 * </p>
	 * <p>
	 * If no corresponding experience has been found, this method will ignore this request.
	 * </p>
	 * @param idStaff the staff identifier
	 * @param experience the experience whose level has to be updated.
	 */
	void updateExperience(int idStaff, Experience experience);

	/**
	 * <p>
	 * Add a project inside the missions of a staff member.
	 * </p>
	 * 
	 * @param idStaff     the staff identifier
	 * @param idProject   the project identifier
	 * @param projectName the name of the project
	 */
	void addMission(int idStaff, int idProject, String projectName);

	/**
	 * <p>
	 * Revoke a project from the missions of a staff member.
	 * </p>
	 * 
	 * @param idStaff   the staff identifier
	 * @param idProject the project identifier
	 */
	void delMission(int idStaff, int idProject);

	/**
	 * <p>
	 * Test the existence of a staff member.
	 * </p>
	 * 
	 * @param idStaff the staff identifier.
	 * @return {@code true} if there is no staff with the given id, {@code false}
	 *         otherwise.
	 */
	boolean hasStaff(int idStaff);

	/**
	 * <p>
	 * Test the eligibility of a staff member to meet a specific criteria.
	 * </p>
	 * 
	 * @param staff   the candidate staff
	 * @param pattern the given pattern to match. <i>The pattern is in the current
	 *                release actually </i>
	 * @return {@code true} if the given staff is matching the passed pattern,
	 *         {@code false} otherwise.
	 */
	public boolean isEligible(Staff staff, String pattern);

	/**
	 * <p>
	 * Test the eligibility of a staff member to meet a specific criteria.
	 * </p>
	 * 
	 * @param staff       the candidate staff
	 * @param pattern     the given pattern to match
	 * @param transformer the string transformer
	 * @return {@code true} if the candidate is matching the given pattern,
	 *         {@code false} otherwise.
	 */
	public boolean isEligible(Staff staff, String pattern, StringTransform transformer);

	/**
	 * Save the <b>encrypted</b> password
	 * 
	 * @param staff    the staff whose password has be saved
	 * @param password the encrypted password
	 */
	public void savePassword(Staff staff, String password);

	/**
	 * Infer the skills for a developer based on his mission and their metrics.
	 * 
	 * @param idStaff the staff identifier
	 * @throws ApplicationException
	 */
	void inferSkillsFromMissions(int idStaff) throws ApplicationException;

	/**
	 * <p>
	 * Process and Update the active status based on the history of missions executed
	 * by the given staff member.
	 * </p>
	 * <p>
	 * This method will update some properties from the given staff object, and mark
	 * the object as <i>"candidate for being serialized on file-system"</i>.
	 * </p>
	 * 
	 * @see DataHandler#saveStaff(Map)
	 * @see StaffController#processActiveStatus(int)
	 * @see #forceActiveStatus(Staff)
	 * @param staff the given developer whose {@code active} status must be switch
	 *              on, or off.
	 */
	void processActiveStatus(Staff staff);

	/**
	 * <p>
	 * <strong>Force</strong> the value of the activity state for a developer.
	 * </p>
	 * <p>
	 * In opposition to the method {@link StaffHandler#processActiveStatus(Staff)}
	 * processActiveStatus} which processes and updates the {@code active} field based
	 * on the activity of this staff member, and <b>on his activity only</b>, this
	 * method forces this value by an end-user decision.
	 * </p>
	 * 
	 * @see #processActiveStatus(Staff)
	 * @param staff the given staff member whose active status has to be switch on,
	 *              or off.
	 */
	void forceActiveStatus(Staff staff);

	/**
	 * <p>
	 * This method returns <strong>{@code false}</strong> if there is at least one
	 * mission with a reference to this project, otherwise
	 * <strong>{@code true}</strong>.
	 * </p>
	 * 
	 * @param idProject the project identifier
	 * @return <strong>{@code true}</strong> if it's a brand new project for all
	 *         staff crew.
	 * @see Project#isEmpty()
	 */
	boolean isProjectReferenced(int idProject);

	/**
	 * @return the next staff identifier available
	 */
	int nextIdStaff();

	/**
	 * Remove a {@link Mission} from the staff contributions.
	 * @param idStaff the Staff identifier
	 * @param idProject the Project identifier
	 * @throws ApplicationException thrown if any problem occurs
	 */
	void removeMission(int idStaff, int idProject) throws ApplicationException;

	/**
	 * Update the  <b>SYSTEM</b> level of a skill for a staff member
	 * @param idStaff the Staff identifier
	 * @param idSkill the Skill identifier
	 * @param level the System level calculated by Hal
	 * @throws ApplicationException thrown if any problem occurs.
	 */
	void updateSkillSystemLevel(int idStaff, int idSkill, int level) throws ApplicationException;

	/**
	 * Save the skills constellations for the current date.
	 * @throws ApplicationException thrown if any exception occurs during the saving process, most probably an {@link IOException}.
	 */
	void saveCurrentConstellations() throws ApplicationException;

	/**
	 * <p>
	 * Load the skills constellations for the given month.
	 * </p>
	 * <em>
	 * An internal control is executed to verify
	 * that each skill-identifier declared in the constellation does still exist effectively. 
	 * </em>
	 * @param month the saving month
	 * @return the resulting list of constellations
	 * @throws ApplicationException thrown if any exception occurs during the saving process, 
	 * most probably 
	 * either an {@link IOException}, 
	 * or there is no constellationn registrered for the given month, 
	 * or an unknown skill identifier has been detected.
	 * @throws NotFoundException thrown if there is no constellation for the given month 
	 */
	List<Constellation> loadConstellations(LocalDate month) throws ApplicationException, NotFoundException;

}
