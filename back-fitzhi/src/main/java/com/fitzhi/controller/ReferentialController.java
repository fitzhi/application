package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_FILE_REFERENTIAL_NOFOUND;
import static com.fitzhi.Error.CODE_IO_EXCEPTION;
import static com.fitzhi.Error.MESSAGE_FILE_REFERENTIAL_NOFOUND;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.fitzhi.bean.RiskProcessor;
import com.fitzhi.data.internal.RiskLegend;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/referential")
@Api(
	tags="Referential controller API",
	description = "API endpoints in charge of the interaction with the referential of data used by the application."
)
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
	
	@ResponseBody
	@GetMapping(value = "/{referential}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String read(@PathVariable("referential") String referentialName) throws ApplicationException, NotFoundException {

		if (log.isDebugEnabled()) {
			log.debug(String.format("Reading the referential %s", referentialName));
		}

		try {
			final File refFile = new File (referentialDir+referentialName+".json"); 
			if (log.isDebugEnabled()) {
				log.debug(String.format("Trying to load the file %s", refFile.getAbsolutePath()));
			}
			if (!refFile.exists()) {
				throw new NotFoundException(CODE_FILE_REFERENTIAL_NOFOUND, 
					MessageFormat.format(MESSAGE_FILE_REFERENTIAL_NOFOUND, referentialName + ".json"));
			} 

			StringBuilder response;
			try (BufferedReader br = new BufferedReader(new FileReader(refFile))) {
				response = br.lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
			}

			return response.toString();

		} catch (IOException ioe) {
			throw new ApplicationException(CODE_IO_EXCEPTION, ioe.getMessage(), ioe);
		} 
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
