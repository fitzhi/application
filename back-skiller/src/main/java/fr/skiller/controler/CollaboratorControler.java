package fr.skiller.controler;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/staff")
public class CollaboratorControler {

	@GetMapping("/all")
	String readAll() {
		return "";	
	}

}
