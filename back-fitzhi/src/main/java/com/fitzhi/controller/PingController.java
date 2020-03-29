package com.fitzhi.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitzhi.controller.sample.LocalDateContainer;

@RestController
@RequestMapping("/api/test")
/**
 * @author Fr&eacute;d&eacute;ric VIDAL Controller for Ping purpose
 */

public class PingController {

	
	@GetMapping("/buildDate")
	public ResponseEntity<LocalDateContainer> buildDate()  {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		LocalDateContainer ldc = new LocalDateContainer(2020, 3, 16);
		return new ResponseEntity<>(
				ldc, 
				headers, 
				HttpStatus.OK);
	}
	
	@GetMapping("/ping")
	public ResponseEntity<String> welcome()  {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_HTML);
		return new ResponseEntity<>(
				"pong", 
				headers, 
				HttpStatus.OK);
	}

	@PostMapping("/pong")
	public ResponseEntity<String> postWelcome()  {		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		return new ResponseEntity<>(
				"ping", 
				headers, 
				HttpStatus.OK);
	}

}
