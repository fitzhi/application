package fr.skiller.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.skiller.bean.RiskProcessor;
import fr.skiller.data.internal.RiskLegend;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/referential")
public class ReferentialController {

	/**
	 * Directory where the referential data are stored.
	 */
	@Value("${referential.dir}")
	private String referentialDir;

	/**
	 * Bean in charge of the evaluation of risks.
	 */
	@Autowired
	@Qualifier("commitAndDevActive")
	RiskProcessor riskProcessor;
	
	@GetMapping(value = "/{referential}")
	public ResponseEntity<String> read(@PathVariable("referential") String referentialName) {

		if (log.isDebugEnabled()) {
			log.debug(String.format("Reading the referential %s", referentialName));
		}

		ResponseEntity<String> responseEntity;
		try {
			final File refFile = new File (referentialDir+referentialName+".json"); 
			if (log.isDebugEnabled()) {
				log.debug(String.format("Trying to load the file %s", refFile.getAbsolutePath()));
			}
			if (!refFile.exists()) {
				responseEntity = new ResponseEntity<>("Referential " + referentialName + " does not exist !", new HttpHeaders(), HttpStatus.NOT_FOUND);
			} else {
				try (BufferedReader br = new BufferedReader(new FileReader(refFile))) {
					StringBuilder response = br.lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
					responseEntity = new ResponseEntity<>(response.toString(), new HttpHeaders(), HttpStatus.OK);
				}
			}
		} catch (IOException ioe) {
			final String errorMessage = "INTERNAL ERROR with file " + referentialName + ".json : " + ioe.getMessage();
			log.error(errorMessage);
			responseEntity = new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		} 
		return responseEntity;
	}
	
	/**
	 * HTTP GET to retrieve the risks legend of the sunburst-chart
	 * @return the risks legend for the actual implementation of {@link RiskProcessor}
	 */
	@GetMapping(value = "/riskLegends")
	public ResponseEntity<List<RiskLegend>> riskLegends() {
		List<RiskLegend> legends = new ArrayList<>(riskProcessor.riskLegends().values());
		return new ResponseEntity<>(legends, new HttpHeaders(), HttpStatus.OK);
	}
}
