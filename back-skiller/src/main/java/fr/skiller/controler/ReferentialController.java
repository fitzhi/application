package fr.skiller.controler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
@RequestMapping("/data")
public class ReferentialController {

	Logger logger = LoggerFactory.getLogger(ReferentialController.class.getCanonicalName());

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson g = new Gson();

	@RequestMapping(value = "/{referential}", method = RequestMethod.GET)
	ResponseEntity<String> read(@PathVariable("referential") String referentialName) {

		if (logger.isDebugEnabled()) {
			logger.debug("Reading the referential " + referentialName);
		}

		String line;
		final StringBuilder sb = new StringBuilder();
		ResponseEntity<String> responseEntity;
		BufferedReader br = null;
		try {
			
			URL url = this.getClass().getResource("/data/" + referentialName + ".json");
			if (url == null) {
				responseEntity = new ResponseEntity<String>("Referential " + referentialName + " does not exist !", new HttpHeaders(), HttpStatus.NOT_FOUND);
			} else {
				br = new BufferedReader(new FileReader(
						new File(url.getPath())));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
			responseEntity = new ResponseEntity<String>(sb.toString(), new HttpHeaders(), HttpStatus.OK);
			}
		} catch (IOException ioe) {
			final String errorMessage = "INTERNAL ERROR with file " + referentialName + ".json : " + ioe.getMessage();
			logger.error(errorMessage);
			responseEntity = new ResponseEntity<String>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return responseEntity;
	}

}
