package fr.skiller.controler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fr.skiller.data.internal.Staff;

@RestController
@RequestMapping("/test")
/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Controller for testing purpose
 */

public class TestController {

	Logger logger = LoggerFactory.getLogger("backend-skiller");

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson g = new Gson();

	/**
	 * Internal Parameters class
	 * @author Fr&eacute;d&eacute;ric VIDAL 
	 */
	class Test {
		public String test;

		@Override
		public String toString() {
			return "Test [test=" + test + "]";
		}
	}

	@GetMapping("/")
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

}
