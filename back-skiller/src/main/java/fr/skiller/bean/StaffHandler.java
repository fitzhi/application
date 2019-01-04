package fr.skiller.bean;

import java.util.List;
import java.util.Map;

import fr.skiller.data.internal.PeopleCountExperienceMap;
import fr.skiller.data.internal.ResumeSkill;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;

/**
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public interface StaffHandler {

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
	PeopleCountExperienceMap countAllStaff_GroupBy_Skill_Level(boolean active);
	
	/**
	 * Add a collection of skills usually extracted from the employe's resume.
	 * @param idStaff the identifier for this staff member
	 * @param skills the skills detected
	 * @return the updated Staff
	 * @throws SkillerException Thrown if any error occurs during the treatment
	 */
	Staff addExperiences (final int idStaff, final ResumeSkill[] skills) throws SkillerException ;
	
	/**
	 * Lookup for staff members responding to a polymorphous criteria.<br/>
	 * For this release, 2 scenarios are implemented regarding the content of this criteria : <br/>
	 * <ul>
	 * <li>The criteria contains ONE word and therefore, it's corresponding to the connection login.</li> 
	 * <li>The criteria contains TWO words and therefore, it's corresponding to user first name and last name.</li> 
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
}
