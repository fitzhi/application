import { Skill } from './skill';
import { Library } from './library';

export class Settings {

	/**
	 * @param urlSonar URL(s) of the Sonar server.
	 */
	constructor(public urlSonar?: string[]) { }

}

