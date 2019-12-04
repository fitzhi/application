import { TaskLog } from './task-log';

export class Task {
	constructor(
		public id: number,
		public operation: string,
		public complete: boolean,
		public title: string,
		public logs: TaskLog[],
		public lastBreath: TaskLog) { }
}
