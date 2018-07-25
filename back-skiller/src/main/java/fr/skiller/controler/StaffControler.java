package fr.skiller.controler;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fr.skiller.data.Collaborator;

@RestController
@RequestMapping("/staff")
public class StaffControler {

	Gson g = new Gson();

	@GetMapping("/all")
	@CrossOrigin(origins = "http://localhost:4200")
	String readAll() {
		
		Collaborator[] staff = new Collaborator[5];
		staff[0] = new Collaborator(1,
		    "Frederic",
		    "VIDAL",
		    "altF4",
		    "frvidal@sqli.com",
		    "ET2");
		staff[1] = new Collaborator(2,
		    "Olivier",
		    "MANFE",
		    "la Mouf",
		    "omanfe@sqli.com",
		    "ICD 3");
		staff[2] = new Collaborator(3,
		    "Alexandre",
		    "JOURDES",
		    "Jose",
		    "ajourdes@sqli.com",
		    "ICD 2");
		staff[3] = new Collaborator(4,
			    "Thomas",
			    "LEVAVASSEUR",
			    "Grg",
			    "tlavavasseur@sqli.com",
			    "ICD 4");
		staff[4] = new Collaborator(5,
		    "Christophe",
		    "OPOIX",
		    "Copo",
		    "ocopoix@sqli.com",
		    "ET 2");
		
		return g.toJson(staff);	
	}

}
