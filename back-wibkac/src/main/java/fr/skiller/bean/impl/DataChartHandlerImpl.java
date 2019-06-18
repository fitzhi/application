package fr.skiller.bean.impl;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fr.skiller.SkillerRuntimeException;
import fr.skiller.bean.DataChartHandler;
import fr.skiller.data.internal.DataChart;

/**
 * <p>
 * Main implementation for <code>DataChartHandler</code>, in charge of working with the commit repository data 
 * without any adherence with the kind of tool for source control (GIT, SVN...)
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 * @version 1.0
 */
@Service
public class DataChartHandlerImpl implements DataChartHandler {

 	private static final String ADDING_S_IN_S = "adding %s in %s";
	/**
 	 * The logger for the GitScanner.
 	 */
	final Logger logger = LoggerFactory.getLogger(DataChartHandlerImpl.class.getCanonicalName());

	@Override
	public DataChart aggregateDataChart(DataChart dataChart) {
		
		DataChart aggregateData = new DataChart("root");
		
		if (dataChart.getChildren() != null) {
			dataChart.getChildren().stream().forEach(
				child -> 
					aggregate(aggregateData, child));
		}
		return aggregateData;
	}
	
	private void aggregate(DataChart resultingData, DataChart data) {
		if (data.getChildren() != null) {
			switch (data.getChildren().size()) {
				case 0:
					if (logger.isDebugEnabled()) {
						logger.debug(String.format(ADDING_S_IN_S, data.getLocation(), resultingData.getLocation()));
					}
					resultingData.addSubDir(data);
					break;
				case 1:
					DataChart uniqueChild = data.getChildren().get(0);
					// This directory has no source file and only ONE sub-directory
					if (data.getNumberOfFiles() == 0) {
						// Defensive test.
						if ((data.getClassnames() != null) && (data.getClassnames().isEmpty())) {
							throw new SkillerRuntimeException("Should not pass here !");
						}
						uniqueChild.setLocation(data.getLocation()+"/"+uniqueChild.getLocation());
						aggregate(resultingData, uniqueChild);
					} else {		
						DataChart subDir = extractLevel(data);
						if (logger.isDebugEnabled()) {
							logger.debug(String.format(ADDING_S_IN_S, subDir.getLocation(), resultingData.getLocation()));
						}
						resultingData.addSubDir(subDir);
						aggregate(subDir, uniqueChild);						
					}
					break;
				default:
					DataChart subDir = extractLevel (data);
					resultingData.addSubDir(subDir);					
					if (logger.isDebugEnabled()) {
						logger.debug(String.format(ADDING_S_IN_S, subDir.getLocation(), resultingData.getLocation()));
					}
					for (DataChart child : data.getChildren()) {
						aggregate (subDir, child);
					}
					break;
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format(ADDING_S_IN_S, data.getLocation(), resultingData.getLocation()));
			}
			resultingData.addSubDir(data);
		}
	}
	
	private DataChart extractLevel (DataChart data) {
		
		DataChart subDir = new DataChart(data.getLocation());
		subDir.setColor(data.getColor());
		subDir.setRiskLevel(data.getRiskLevel());
		if (data.getClassnames() != null) {
			data.getClassnames().forEach(subDir::addSource);
		}
		return subDir;
	}
	
}
