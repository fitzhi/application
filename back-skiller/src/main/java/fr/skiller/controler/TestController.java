package fr.skiller.controler;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

import fr.skiller.bean.CacheDataHandler;
import fr.skiller.data.internal.Test;
import fr.skiller.source.scanner.RepoScanner;

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

	/**
	 * Source control parser.
	 */
	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	/**
	 * Cache Handler
	 */
	@Autowired
	CacheDataHandler cacheDataHandler;

	private static File resourcesDirectory = new File("src/main/resources");

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

}
