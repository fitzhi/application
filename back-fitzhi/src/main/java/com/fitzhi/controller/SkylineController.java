package com.fitzhi.controller;

import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.data.internal.Skyline;
import com.fitzhi.exception.SkillerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Controller in charge of the generation of skyline.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class SkylineController {

	/**
	 * Service in charge of the generation of the rising skyline data.
	 */
	@Autowired
	SkylineProcessor skylineProcessor;


	@GetMapping("/skyline")
	public ResponseEntity<Skyline> skyline() throws SkillerException {

		if (log.isDebugEnabled()) {
			log.debug("GET command /skykine");
		}

		Skyline skyline = skylineProcessor.generateSkyline();
		if (log.isDebugEnabled()) {
			log.debug(String.format("Returning a skykine of %d entries", skyline.getFloors().size()));
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType (MediaType.APPLICATION_JSON_UTF8);

		return new ResponseEntity<>(skyline, headers, HttpStatus.OK);

	}

}
