/**
 * Clas representing a legend of a risk.
 */
export class RiskLegend {

	/**
	 * @param level level of risk (from 0 to 10).
	 * @param color color associated to the color.
	 * @param descrition Explanation for this level of risk.
	 */
	constructor(
		public level?: number,
		public color?: string,
		public description?: string) {}

}
