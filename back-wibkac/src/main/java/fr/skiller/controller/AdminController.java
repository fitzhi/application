package fr.skiller.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.skiller.bean.Administration;

@RestController
@RequestMapping("/admin")
/**
 * @author Fr&eacute;d&eacute;ric VIDAL Controller for Ping purpose
 */

public class AdminController {

	@Autowired
	private Administration administration;
	
	@GetMapping("/isfirstConnection")
	public ResponseEntity<Boolean> welcome()  {
		
		boolean isVeryFirstConnection = administration.isVeryFirstConnection();
		
		return new ResponseEntity<>(
				isVeryFirstConnection, 
				new HttpHeaders(), 
				HttpStatus.OK);
	}

}
