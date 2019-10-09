export class ProjectSonarMetricValue {
	/**
	 * Constructor.
	 * @param metric the key of the metric
	 * @param weight the weight of this metric in this global evaluation
	 * @param value the value retrieved for this metric.
	 */
	constructor(
		public metric: string,
		public weight: number,
		public value: number) {}
}
