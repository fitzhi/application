package fr.skiller.controler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import fr.skiller.Global;
import fr.skiller.data.JsonTest;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Skill;
import fr.skiller.data.internal.SunburstData;
import fr.skiller.data.internal.Test;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.data.source.ConnectionSettings;
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

	@GetMapping("/sunburst-test")
	ResponseEntity<SunburstData> testSunburst() {
		
		if (logger.isDebugEnabled()) {
			logger.debug("entering testSunburst...");
		}
		
		SunburstData gd = null;
		try {
			gd = getTestingValue();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final MultiValueMap<String, String> headers = new HttpHeaders();
		final ResponseEntity<SunburstData> responseEntity = new ResponseEntity<SunburstData>(gd, headers, HttpStatus.OK);
		if (logger.isDebugEnabled()) {
			logger.debug(Global.LN+gd.toString());
		}
		return responseEntity;
	}

	
	private SunburstData getTestingValue() throws Exception {
		
		Gson gson = new GsonBuilder().create();
		File input = new File(resourcesDirectory.getAbsolutePath() + "/root.json");
		if (input.exists()) {
			JsonReader reader = gson.newJsonReader(new FileReader(input));
			Type SunburstDataType = new TypeToken<SunburstData>() {
			}.getType();
			SunburstData data = new SunburstData("root");
			data = gson.fromJson(reader, SunburstDataType);
			if (logger.isDebugEnabled()) {
				logger.debug("returning the data from " + input.getAbsolutePath());
			}
			return data;
		}
		
		Project project = new Project(1, "VEGEO");
		final String fileProperties = resourcesDirectory.getAbsolutePath() + "/repository-settings/properties-VEGEO.json";

		ConnectionSettings settings = new ConnectionSettings();
		final FileReader fr = new FileReader(new File(fileProperties));
		settings = gson.fromJson(fr, settings.getClass());
		fr.close();
		if (logger.isDebugEnabled()) {
			logger.debug("GIT remote URL " + settings.url);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("cloning...");
		}
		scanner.clone(project, settings);
		if (logger.isDebugEnabled()) {
			logger.debug("...cloned");
		}
        
		if (logger.isDebugEnabled()) {
			logger.debug("parsing...");
		}
		final CommitRepository repo = scanner.parseRepository(project, settings);
		if (logger.isDebugEnabled()) {
			logger.debug("...parsed");
			logger.debug(repo.size() + " records in the repository");
		}
        
		SunburstData data = scanner.aggregateSunburstData(repo);
		
		if (logger.isDebugEnabled()) {
			Gson g = new Gson();
			String content = g.toJson(data);
			File output = new File(resourcesDirectory.getAbsolutePath() + "/root.json");
			logger.debug("Writing result into " + output.getAbsolutePath());
			BufferedWriter bw = new BufferedWriter(new FileWriter(output));
			bw.write(content);
			bw.close();
			System.out.println(" ");
			System.out.println(content); 
			System.out.println(" ");
		}
		
		
		return data;
	}
}
