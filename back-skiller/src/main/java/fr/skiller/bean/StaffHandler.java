package fr.skiller.bean;

import java.util.Map;

import fr.skiller.data.Collaborator;
import fr.skiller.data.Project;

/**
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public interface StaffHandler {

	Map<Integer, Collaborator> getStaff();

}
