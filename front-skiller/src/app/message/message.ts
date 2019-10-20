/**
 * Class of message
 */
export class Message {

	constructor(private severity: number, private message: string) {
	}

	trace() {
		return this.severity + ' ' + this.message;
	}
}
