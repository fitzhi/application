package com.fitzhi.bean;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fitzhi.bean.impl.StringTransform;
import com.fitzhi.controller.StaffController;
import com.fitzhi.data.internal.Experience;
import com.fitzhi.data.internal.PeopleCountExperienceMap;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ResumeSkill;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.data.internal.Author;

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
	 * @param isStaff : identified of this new employee
	 * @param staff   : Staff instance representing this employee
	 * @return the previous staff member associated with this idStaff, or null if
	 *         there was no mapping for this id.
	 */
	Staff put(final int idStaff, final Staff staff);

	/**
	 * Remove a a staff member from the personal.
	 * 
	 * @param isStaff : identifier of the employee to be deleted.
	 * @return the staff member just deleted, or {@code null} if none exists.
	 */
	Staff removeStaff(final int idStaff);

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
	 * Lookup for staff members responding to a polymorphous criteria.<br/>
	 * For this release, 2 scenarios are implemented regarding the content of this
	 * criteria : <br/>
	 * <ul>
	 * <li>The author email has a corresponding staff member with the same
	 * email.</li>
	 * <li>The author name contains ONE word and therefore this name is
	 * corresponding either to the connection login, or the last name, or the first
	 * name <i>(in that order)</i>.</li>
	 * <li>The author name contains MULTIPLE words and therefore, it's corresponding
	 * to user full name (first + last) or (last + first).</li>
	 * </ul>
	 * 
	 * @param criteria polymorphous author used to lookup for a staff member
	 * @return the <i>first</i> staff corresponding to the criteria, or NULL is
	 *         none's found
	 */
	Staff lookup(Author author);

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
	 * Involve a single contributors into the project.<br/>
	 * The method add, or update, the missions for the staff associated with the
	 * contributor.
	 * </p>
	 * 
	 * @param project     the current project
	 * @param contributor the contributor data representing the contribution of a
	 *                    staff member in a project.
	 * @throws ApplicationException thrown if any problem occurs
	 */
	void involve(Project project, Contributor contributor) throws ApplicationException;

	/**
	 * <p>
	 * Retrieve the contributors list for the given project.
	 * </p>
	 * 
	 * @param project the project identifier.
	 * @return the list of contributors, which might be empty (but not
	 *         <code>null</code>)
	 */
	List<Contributor> getContributors(int projectId);

	/**
	 * @param idStaff the staff identifier
	 * @return the full name of the staff member found, or {@code null} is none's
	 *         found
	 */
	String getFullname(int idStaff);

	/**
	 * @param ifStaff the passed staff identifier
	 * @return {@code true} if a project exists for this project identifier,
	 *         {@code false} otherwise
	 */
	boolean containsStaffMember(int idStaff);

	/**
	 * Validate the given staff member.
	 * <p>
	 * Validation rules migt be :
	 * <ul>
	 * <li>This given staff does not yet exist <em>for the update
	 * operation</em></li>
	 * <li>The same {@code login} already exists in the workforce.</li>
	 * </ul>
	 * </p>
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
	 * Update THE LEVEL ONLY of an experience.
	 * </p>
	 * 
	 * @param idStaff    the staff identifier
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
	 * Get and return a staff member given his identifier.
	 * </p>
	 * 
	 * @param idStaff the staff identifier.
	 * @return the selected staff or {@code null} if none exists.
	 */
	Staff getStaff(int idStaff);

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
	 * Process & Update the active status based on the history of missions executed
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
	 * </p>
	 * <b>Force</b> the value of the activity state for a developer.
	 * </p>
	 * <p>
	 * In opposition to the method {@link StaffHandler#processActiveStatus(Staff)
	 * processActiveStatus} which processes & updates the {@code active} field based
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

}
