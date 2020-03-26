/**
 * Log associated to the activity.
 */
export class ActivityLog {

	public timestamp: number;
	public code: number;
	public message: string;
	public complete: string;
	public completeOnError: string;

	constructor(jsonData) {
		Object.assign(this, jsonData);
	}
	/**
	 * Return *true* if the treatment is OK, *false* otherwise.
	 */
	isOk(): boolean {
		return (this.code === 0);
	}

	/**
	 * Return *true* if the treatment is KO, *false* otherwise.
	 */
	isKo(): boolean {
		return (this.code !== 0);
	}
}
