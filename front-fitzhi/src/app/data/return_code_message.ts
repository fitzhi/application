/**
 * Returned object from DialogBox
 */
export class ReturnCodeMessage {

	public code: number;
	public message: string;

	/**
	 * Public constructor.
	 */
	constructor(code: number, message: string) {
		this.code = code;
		this.message = message;
	}

}
