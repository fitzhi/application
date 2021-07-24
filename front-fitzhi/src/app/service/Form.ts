import { Constants } from '../constants';
import { traceOn } from '../global';

export class Form {

	/**
	 * Active form identifier.
	 */
	public formIdentifier = -1;

	/**
	 * url activated for this form.
	 */
	public url: string;

	/**
	 * Constructor
	 */
	constructor(formIdentifier: number, url: string) {
		this.formIdentifier = formIdentifier;
		this.url = url;
	}

	public trace() {
		if (traceOn()) {
			console.log(`Form Identifier ${Constants.CONTEXT[this.formIdentifier]} for url ${this.url}`);
		}
	}
}
