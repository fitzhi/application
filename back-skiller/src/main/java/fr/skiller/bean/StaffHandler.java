package fr.skiller.bean;

import java.util.Map;

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
	
	Map<Integer, Staff> getStaff();

}
