package com.fitzhi.bean;

import com.fitzhi.data.internal.DataChart;

/**
 * <p>
 * Interface in charge of handling the hierarchical collection representing the source repository commit history.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 * @version 1.0
 */
public interface DataChartHandler {

	/**
	 * <p>
	 * Compact the dashboard by aggregating empty directories.
	 * </p>
	 * e.g. 
	 * A tree representing a class like <code>fr.common.my-package.MyClass"</code> might create 3 nodes of <code>RiskChartData</code>.
	 * <ul>
	 * <li>one for <code><b>fr</b></code></li>
	 * <li>one for <code><b>common</b></code></li>
	 * <li>one for <code><b>my-package</b></code></li>
	 * </ul>
	 * <p>
	 * <b>BUT</b> Possibly, there is no source files present in <code><b>fr</b></code>. 
	 * So instead of keeping 2 levels of hierarchy (with an empty one), 
	 * it would be easier to aggregate the 2 directories into the resulting one : <code>fr.my-package</code>
	 * </p>
	 * @param dataChart the given data chart
	 */
	void aggregateDataChart(DataChart dataChart);

}
