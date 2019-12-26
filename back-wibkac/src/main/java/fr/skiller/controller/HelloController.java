package fr.skiller.controller;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.skiller.data.internal.Mission;

@RestController
@RequestMapping("/api/globzhi")
public class HelloController {

	
	@PostMapping
	public ResponseEntity<String> addHello(@RequestBody String input) {
		return ResponseEntity.ok("{ hello:\"World\" }");
	}

	@GetMapping("/helloWorld")
	public String readHello() {
		return "{ hello:\"World\" }";
	}

	@GetMapping("/entityLocaldate")
	public ResponseEntity<LocalDate> getEntityLocalDate() {
		return new ResponseEntity<LocalDate>(LocalDate.of(2019, 10, 9), HttpStatus.OK);
	}

	@GetMapping("/mission")
	public ResponseEntity<Mission> getMission() {
		System.out.println("getMission()");
		Mission m = new Mission(1, 2, "TEST");
		m.setFirstCommit(LocalDate.of(2019, 10, 9));
		System.out.println("m " + m);
		return new ResponseEntity<Mission>(m, HttpStatus.OK);
	}

}