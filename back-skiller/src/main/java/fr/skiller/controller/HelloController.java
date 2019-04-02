package fr.skiller.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/back-skiller")
public class HelloController {

	
	@PostMapping
	public ResponseEntity<String> addHello(@RequestBody String input) {
		return ResponseEntity.ok("{ hello:\"World\" }");
	}

	@GetMapping("/helloWorld")
	public String readHello() {
		return "{ hello:\"World\" }";
	}

}