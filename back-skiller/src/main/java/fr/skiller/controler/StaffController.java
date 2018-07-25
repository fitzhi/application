package fr.skiller.controler;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fr.skiller.data.Collaborator;

@RestController
@RequestMapping("/staff")
public class StaffController {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson g = new Gson();

	/**
	 * The staff collection.
	 */
	private ArrayList<Collaborator> staff;
	
	/**
	 * @return the staff collection.
	 */
	private ArrayList<Collaborator> getStaff() {
		if (this.staff != null) {
			return this.staff;
		}
		this.staff = new ArrayList<Collaborator>();
		staff.add(new Collaborator(1,
		    "Frederic",
		    "VIDAL",
		    "altF4",
		    "frvidal@sqli.com",
		    "ET2"));
		staff.add(new Collaborator(2,
		    "Olivier",
		    "MANFE",
		    "la Mouf",
		    "omanfe@sqli.com",
		    "ICD 3"));
		staff.add(new Collaborator(3,
		    "Alexandre",
		    "JOURDES",
		    "Jose",
		    "ajourdes@sqli.com",
		    "ICD 2"));
		staff.add(new Collaborator(4,
			    "Thomas",
			    "LEVAVASSEUR",
			    "Grg",
			    "tlavavasseur@sqli.com",
			    "ICD 4"));
		staff.add(new Collaborator(5,
		    "Christophe",
		    "OPOIX",
		    "Copo",
		    "ocopoix@sqli.com",
		    "ET 2"));
		return staff;
	}
	
	@RequestMapping(value="/{idParam}", method = RequestMethod.GET)
	@CrossOrigin(origins = "http://localhost:4200")
	String read(@PathVariable("idParam") int idParam) {
		
		Optional<Collaborator> searchCollab = getStaff().stream().filter (c -> (c.id == idParam)).findFirst();
 		return g.toJson(searchCollab);
	}
	
	@GetMapping("/all")
	@CrossOrigin(origins = "http://localhost:4200")
	String readAll() {
		return g.toJson(getStaff());	
	}

}
