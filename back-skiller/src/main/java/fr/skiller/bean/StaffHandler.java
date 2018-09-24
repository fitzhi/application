package fr.skiller.bean;

import java.util.Map;

import fr.skiller.data.internal.CountSkillLevelMap;
import fr.skiller.data.internal.Staff;

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
	  * @param active <code>true</code> Only active developers are taking account, <code>false</code> all developers are included. 
	  * @return the number of developers group by skills registered into a Map.
	  */
	CountSkillLevelMap countAllStaff_GroupBy_Skill_Level(boolean active);
	

}
