/**
 * Log associated to the activity.
 */
export class ActivityLog {

	public timestamp: number;
	public code: number;
	public message: string;
	public complete: boolean;
	public completeOnError: boolean;

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
	 * Return *true* if the treatment is COMPLETE AND OK, *false* otherwise.
	 */
	isCompleteOk(): boolean {
		return (this.code === 0) && this.complete;
	}

	/**
	 * Return *true* if the treatment is KO, *false* otherwise.
	 */
	isKo(): boolean {
		return (this.code !== 0);
	}

	/**
	 * Return *true* if the treatment is COMPLETE AND KO, *false* otherwise.
	 */
	isCompleteKo(): boolean {
		return (this.code === 0) && this.completeOnError;
	}
}
