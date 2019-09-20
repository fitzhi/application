/**
 * Metrics project selected.
 */
export class ProjectSonarMetric {

	/**
	 * Public constructor.
	 * @param key The metric key.
	 * @param name The metric key.
	 * @param selected Is this metric selected ?
	 * @param weight: weight of this metric in the global Sonar evaluation.
	 */
	constructor(
		public key: string,
		public name: string,
		public selected: boolean,
		public weight:  number) {
	}
}
