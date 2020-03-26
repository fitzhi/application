export class TaskLog {

	public timestamp: number;
	public code: number;
	public message: string;

	constructor(jsonData) {
		Object.assign(this, jsonData);
	}

}
