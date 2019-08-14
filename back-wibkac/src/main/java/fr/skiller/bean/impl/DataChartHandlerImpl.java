package fr.skiller.bean.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

	/**
 	 * The logger for the GitScanner.
 	 */
	final Logger logger = LoggerFactory.getLogger(DataChartHandlerImpl.class.getCanonicalName());

	@Override
	public void aggregateDataChart(DataChart dataChart) {
		if (dataChart.getChildren() != null) {
			dataChart.getChildren().stream().forEach(this::aggregate);
		}
	}

	private void aggregate(DataChart data) {
		if ( (data.getChildren() != null) && !(data.getChildren().isEmpty()) ) {
			if (data.getChildren().size() == 1) {
				DataChart uniqueChild = data.getChildren().get(0);
				// This directory has no source file and only ONE sub-directory
				if (data.getNumberOfFiles() == 0) {
					data.aggregate(uniqueChild);
					aggregate(data);
					return;
				}
			}
			for (DataChart child : data.getChildren()) {
				aggregate (child);
			}
		}
	}
	
}
