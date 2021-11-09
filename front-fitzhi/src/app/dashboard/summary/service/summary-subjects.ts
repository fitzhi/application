export class SummarySubjects {
	constructor(public overallAverage = false) { }

	/**
	 * @returns {@code true} if there is no summary to be displayed/ 
	 */
	public noData() {
		return !(this.overallAverage);
	}
}
