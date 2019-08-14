package fr.skiller.bean;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.skiller.data.internal.PeopleCountExperienceMap;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.ResumeSkill;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.source.Contributor;
import fr.skiller.exception.SkillerException;

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
	  * Control and save the given staff.s
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

}

