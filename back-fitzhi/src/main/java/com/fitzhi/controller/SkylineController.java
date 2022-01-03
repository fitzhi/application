package com.fitzhi.controller;

import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.data.internal.Skyline;
import com.fitzhi.exception.ApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(
	tags="Skyline controller API",
	description = "API endpoints in charge of the generation of the rising skyline data."
)
public class SkylineController {

	/**
	 * Service in charge of the generation of the rising skyline data.
	 */
	@Autowired
	SkylineProcessor skylineProcessor;
	
	@ResponseBody
	@ApiOperation(
		value = "Generate the data required for the rising skyline widget."
	)
	@GetMapping("/skyline")
	public Skyline skyline() throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug("GET verb /api/skykine");
		}

		Skyline skyline = skylineProcessor.generateSkyline();
		if (log.isDebugEnabled()) {
			log.debug(String.format("Returning a skykine of %d entries", skyline.getFloors().size()));
		}

		return skyline;
	}

}
