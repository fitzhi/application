package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static com.fitzhi.Error.CODE_STAFF_NOFOUND;
import static com.fitzhi.Error.MESSAGE_PROJECT_NOFOUND;
import static com.fitzhi.Error.MESSAGE_STAFF_NOFOUND;
import static com.fitzhi.Error.getStackTrace;
import static com.fitzhi.Global.BACKEND_RETURN_CODE;
import static com.fitzhi.Global.BACKEND_RETURN_MESSAGE;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.util.ProjectLoader;
import com.fitzhi.controller.util.ProjectLoader.MyReference;
import com.fitzhi.data.internal.Library;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Skyline;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.SkillerException;

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
			log.debug(String.format("Returning a skykine of %d entries", skyline.getSkyline().size()));
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType (MediaType.APPLICATION_JSON_UTF8);

		return new ResponseEntity<>(skyline, headers, HttpStatus.OK);

	}

}
