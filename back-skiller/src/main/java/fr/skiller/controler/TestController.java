package fr.skiller.controler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fr.skiller.Global;
import fr.skiller.data.internal.CodeDir;
import fr.skiller.data.internal.Test;

@RestController
@RequestMapping("/test")
/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Controller for testing purpose
 */

public class TestController {

	Logger logger = LoggerFactory.getLogger(TestController.class.getCanonicalName());

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson g = new Gson();


	@GetMapping("/get")
	ResponseEntity<Test> test() {

		final ResponseEntity<Test> responseEntity;
		final MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add("test", "test OK MVP");
		Test t = new Test();
		t.test = "Ok";
		
		responseEntity = new ResponseEntity<Test>(t, headers, HttpStatus.OK);
		if (logger.isDebugEnabled()) {
			logger.debug(t.toString());
			logger.debug(headers.toString());
		}
		return responseEntity;
	}

	@PostMapping("/post_a_String")
	ResponseEntity<String> verySimple_post_a_String(@RequestBody String input) {

		if (logger.isDebugEnabled()) {
			logger.debug("Input " + input);
		}
		
		final ResponseEntity<String> responseEntity;
		final MultiValueMap<String, String> headers = new HttpHeaders();
		String test = input + " OK";
		
		responseEntity = new ResponseEntity<String>(test, headers, HttpStatus.OK);
		if (logger.isDebugEnabled()) {
			logger.debug(test.toString());
		}
		return responseEntity;
	}
	
	@PostMapping("/post_a_Test")
	ResponseEntity<Test> verySimple_post_a_Test(@RequestBody Test input) {

		if (logger.isDebugEnabled()) {
			logger.debug("Input.test " + input.test);
		}
		
		final ResponseEntity<Test> responseEntity;
		final MultiValueMap<String, String> headers = new HttpHeaders();
		Test test = new Test(input.test + " OK");
		
		responseEntity = new ResponseEntity<Test>(test, headers, HttpStatus.OK);
		if (logger.isDebugEnabled()) {
			logger.debug(test.toString());
			logger.debug(headers.toString());
		}
		return responseEntity;
	}

	@GetMapping("/sunburst-test")
	ResponseEntity<CodeDir> testSunburst() {
		
		if (logger.isDebugEnabled()) {
			logger.debug("entering testSunburst...");
		}
		CodeDir gd = getTestingValue();
		
		final MultiValueMap<String, String> headers = new HttpHeaders();
		final ResponseEntity<CodeDir> responseEntity = new ResponseEntity<CodeDir>(gd, headers, HttpStatus.OK);
		if (logger.isDebugEnabled()) {
			logger.debug(Global.LN+gd.toString());
		}
		return responseEntity;
	}

	
	private CodeDir getTestingValue() {
		
		CodeDir gRoot = new CodeDir("VEGEO");
//		gRoot.numberOfFiles = 20;
		gRoot.lastUpdate="The 1st of december";
		
		CodeDir g1 = new CodeDir("com");
//		g1.numberOfFiles = 15;
		
		CodeDir g1_bis = new CodeDir("fr");
		g1_bis.numberOfFiles = 5;

		gRoot.addsubDir(g1);
		gRoot.addsubDir(g1_bis);
		
		
		CodeDir g2 = new CodeDir("google");
		g2.numberOfFiles = 5;

		CodeDir g3 = new CodeDir("amazon");
		g3.numberOfFiles = 10;

		g1.addsubDir(g2);
		g1.addsubDir(g3);

		return gRoot;
	}
}
