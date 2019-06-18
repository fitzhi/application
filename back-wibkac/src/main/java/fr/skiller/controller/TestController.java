package fr.skiller.controller;

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
import fr.skiller.data.internal.ForTest;
import fr.skiller.source.crawler.RepoScanner;

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
	public ResponseEntity<ForTest> test() {

		final ResponseEntity<ForTest> responseEntity;
		final MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add("test", "test OK MVP");
		ForTest t = new ForTest();
		t.setTest("Ok");
		
		responseEntity = new ResponseEntity<>(t, headers, HttpStatus.OK);
		if (logger.isDebugEnabled()) {
			logger.debug(t.toString());
			logger.debug(headers.toString());
		}
		return responseEntity;
	}

	@PostMapping("/post_a_String")
	public ResponseEntity<String> verySimplePostString(@RequestBody String input) {

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Input %s", input));
		}
		
		final ResponseEntity<String> responseEntity;
		final MultiValueMap<String, String> headers = new HttpHeaders();
		String test = input + " OK";
		
		responseEntity = new ResponseEntity<>(test, headers, HttpStatus.OK);
		if (logger.isDebugEnabled()) {
			logger.debug(responseEntity.toString());
		}
		return responseEntity;
	}
	
	@PostMapping("/post_a_Test")
	public ResponseEntity<ForTest> verySimplePostTest(@RequestBody ForTest input) {

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Input.test : %s", input.getTest()));
		}
		
		final ResponseEntity<ForTest> responseEntity;
		final MultiValueMap<String, String> headers = new HttpHeaders();
		ForTest test = new ForTest(input.getTest() + " OK");
		
		responseEntity = new ResponseEntity<>(test, headers, HttpStatus.OK);
		if (logger.isDebugEnabled()) {
			logger.debug(test.toString());
			logger.debug(headers.toString());
		}
		return responseEntity;
	}

}
