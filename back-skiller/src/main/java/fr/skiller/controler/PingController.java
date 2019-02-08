package fr.skiller.controler;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.skiller.bean.ProjectHandler;

@RestController
@RequestMapping("/ping")
/**
 * @author Fr&eacute;d&eacute;ric VIDAL Controller for Ping purpose
 */

public class PingController {

	@Autowired
	ProjectHandler projectHandler;
	
	@GetMapping("")
	ResponseEntity<String> pong() throws Exception {
		return new ResponseEntity<String>("pong", new HttpHeaders(), HttpStatus.OK);
	}

}
