package com.tixhi.bean.impl;

import static com.tixhi.Error.CODE_FILE_REFERENTIAL_NOFOUND;
import static com.tixhi.Error.CODE_IO_ERROR;
import static com.tixhi.Error.MESSAGE_FILE_REFERENTIAL_NOFOUND;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tixhi.bean.SonarHandler;
import com.tixhi.data.internal.ProjectSonarMetricValue;
import com.tixhi.exception.SkillerException;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Bean in  charge of the implementation of the interface {@link SonarHandler}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
@Component
public class SonarHandlerImpl implements SonarHandler {

	/**
	 * Directory where the referential data are stored.
	 */
	@Value("${referential.dir}")
	private String referentialDir;
	
	/**
	 * Name of the file containing the metrics supported by our system.
	 */
	@Value("${supportedMetrics.file}")
	private String supportedMetricsFile;
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	private static Gson gson = new GsonBuilder().create();

	/**
	 * List of default Sonar metrics.
	 */
	private List<ProjectSonarMetricValue> defaultMetrics = null;
		
	@Override
	public List<ProjectSonarMetricValue> getDefaultProjectSonarMetrics() throws SkillerException {
		if (defaultMetrics == null) {
			defaultMetrics = new ArrayList<ProjectSonarMetricValue>();
			String fileMetricsContent = loadDefaultMetrics();

			Type listMetricsType = new TypeToken<List<ProjectSonarMetricValue>>(){}.getType();
			List<ProjectSonarMetricValue> metrics = gson.fromJson(fileMetricsContent, listMetricsType);
			metrics.stream().forEach(metric -> {
				if (metric.getWeight() > 0) {
					if (log.isDebugEnabled()) {
						log.debug(String.format ("%s is a relevant default metric with the weight %d", metric.getKey(), metric.getWeight()));
					}
					defaultMetrics.add(metric);	
				}
			});
		}
		return defaultMetrics;
	}

	@SuppressWarnings("unused")
	private String loadDefaultMetrics() throws SkillerException {
		File refFile = null;
		try {
			refFile = new File (referentialDir+supportedMetricsFile); 
			if (log.isDebugEnabled()) {
				log.debug(String.format("Trying to load the file %s", refFile.getAbsolutePath()));
			}
			if (!refFile.exists()) {
				throw new SkillerException(CODE_FILE_REFERENTIAL_NOFOUND, MessageFormat.format(MESSAGE_FILE_REFERENTIAL_NOFOUND, refFile.getAbsolutePath()));
			} 
			try (BufferedReader br = new BufferedReader(new FileReader(refFile))) {
				StringBuilder response = br.lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
				return response.toString();
			}
		} catch (IOException ioe) {
			if (refFile != null) {
				final String errorMessage = "INTERNAL ERROR with file " + refFile.getAbsolutePath() + ".json : " + ioe.getMessage();
				log.error(errorMessage);

				throw new SkillerException(CODE_IO_ERROR, errorMessage);
			}
		} 
		return "Supposed to be unused code";
	}
}
