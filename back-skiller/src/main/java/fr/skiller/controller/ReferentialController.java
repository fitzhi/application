package fr.skiller.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.sun.javafx.charts.Legend;

import fr.skiller.bean.RiskProcessor;
import fr.skiller.data.internal.RiskLegend;

@RestController
@RequestMapping("/data")
public class ReferentialController {

	Logger logger = LoggerFactory.getLogger(ReferentialController.class.getCanonicalName());

	/**
	 * Initialization of the Google JSON parser.
	 */
	final Gson g = new Gson();

	/**
	 * Bean in charge of the evaluation of risks.
	 */
	@Autowired
	@Qualifier("commitAndDevActive")
	RiskProcessor riskProcessor;
	
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

	/**
	 * HTTP GET to retrierve the risks legend of the sunburst-chart/
	 * @return the risks legend for the actual implementation of {@link RiskProcessor}
	 */
	@RequestMapping(value = "/riskLegends", method = RequestMethod.GET)
	ResponseEntity<List<RiskLegend>> riskLegends() {
		List<RiskLegend> legends = new ArrayList<RiskLegend>(riskProcessor.riskLegends().values());
		return new ResponseEntity<List<RiskLegend>>(legends, new HttpHeaders(), HttpStatus.OK);
	}
}
