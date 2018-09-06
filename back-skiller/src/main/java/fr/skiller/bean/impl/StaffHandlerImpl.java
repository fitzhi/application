package fr.skiller.bean.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Staff;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Component("mock.Staff")
public class StaffHandlerImpl implements StaffHandler {

	/**
	 * The Project collection.
	 */
	private HashMap<Integer, Staff> staff;

	public void init() {
		staff = null;
	}
	
	/**
	 * @return the Project collection.
	 */
	public Map<Integer, Staff> getStaff() {
		if (this.staff != null) {
			return this.staff;
		}
		this.staff = new HashMap<Integer, Staff>();
		staff.put(1, new Staff(1,
			    "Frederic",
			    "VIDAL",
			    "altF4",
			    "frvidal@sqli.com",
			    "ET 2"));
			staff.put(2, new Staff(2,
			    "Olivier",
			    "MANFE",
			    "la Mouf",
			    "omanfe@sqli.com",
			    "ICD 3"));
			staff.put(3, new Staff(3,
			    "Alexandre",
			    "JOURDES",
			    "Jose",
			    "ajourdes@sqli.com",
			    "ICD 2"));
			staff.put(4, new Staff(4,
				    "Thomas",
				    "LEVAVASSEUR",
				    "Grg",
				    "tlavavasseur@sqli.com",
				    "ICD 4"));
			staff.put(5, new Staff(5,
			    "Christophe",
			    "OPOIX",
			    "Copo",
			    "ocopoix@sqli.com",
			    "ET 2"));
			return staff;
	}

}
