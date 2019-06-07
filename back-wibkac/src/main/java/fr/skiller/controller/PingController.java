package fr.skiller.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
/**
 * @author Fr&eacute;d&eacute;ric VIDAL Controller for Ping purpose
 */

public class PingController {

	@GetMapping("/ping")
	public ResponseEntity<String> welcome()  {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_HTML);
		return new ResponseEntity<>(
				"<big>Pong !</big><p>Wibkac 0.1-SNAPSHOT</p><p>Back-end is available.</p>", 
				headers, 
				HttpStatus.OK);
	}

	@PostMapping("/ping")
	public ResponseEntity<String> postWelcome()  {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		return new ResponseEntity<>(
				"pong", 
				headers, 
				HttpStatus.OK);
	}

}
