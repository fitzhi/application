package fr.skiller.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
/**
 * @author Fr&eacute;d&eacute;ric VIDAL Controller for Ping purpose
 */

public class PingController {

	@GetMapping("")
	public ResponseEntity<String> welcome()  {
		return new ResponseEntity<>(
				"<big>Pong !</big><p>Wibkac 0.1-SNAPSHOT</p><p>Back-end is available.</p>", 
				new HttpHeaders(), 
				HttpStatus.OK);
	}

}
