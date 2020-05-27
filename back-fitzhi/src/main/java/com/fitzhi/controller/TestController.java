package com.fitzhi.controller;

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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.data.internal.ForTest;
import com.fitzhi.source.crawler.RepoScanner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/test")
/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Controller for testing purpose
 */

public class TestController {

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

	@GetMapping("/get")
	public ResponseEntity<ForTest> test() {

		final ResponseEntity<ForTest> responseEntity;
		final MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add("test", "test OK MVP");
		ForTest t = new ForTest();
		t.setTest("Ok");
		
		responseEntity = new ResponseEntity<>(t, headers, HttpStatus.OK);
		if (log.isDebugEnabled()) {
			log.debug(t.toString());
			log.debug(headers.toString());
		}
		return responseEntity;
	}

	@PostMapping("/post_a_String")
	public ResponseEntity<String> verySimplePostString(@RequestBody String input) {

		if (log.isDebugEnabled()) {
			log.debug(String.format("Input %s", input));
		}
		
		final ResponseEntity<String> responseEntity;
		final MultiValueMap<String, String> headers = new HttpHeaders();
		String test = input + " OK";
		
		responseEntity = new ResponseEntity<>(test, headers, HttpStatus.OK);
		if (log.isDebugEnabled()) {
			log.debug(responseEntity.toString());
		}
		return responseEntity;
	}
	
	@PostMapping("/post_a_Test")
	public ResponseEntity<ForTest> verySimplePostTest(@RequestBody ForTest input) {

		if (log.isDebugEnabled()) {
			log.debug(String.format("Input.test : %s", input.getTest()));
		}
		
		final ResponseEntity<ForTest> responseEntity;
		final MultiValueMap<String, String> headers = new HttpHeaders();
		ForTest test = new ForTest(input.getTest() + " OK");
		
		responseEntity = new ResponseEntity<>(test, headers, HttpStatus.OK);
		if (log.isDebugEnabled()) {
			log.debug(test.toString());
			log.debug(headers.toString());
		}
		return responseEntity;
	}

}
