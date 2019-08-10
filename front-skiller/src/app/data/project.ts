import { Skill } from './skill';
import { Library } from './library';

export class Project {

	public id: number;
	public name: string;
	public connectionSettings: number;
	public urlRepository: string;
	public username: string;
	public password: string;
	public filename: string;
	public risk: number;
	public skills: Skill[];

	/**
	 * Array of dependeny paths (detected or declared) for this project.
	 */
	public libraries: Library[];

	constructor() { }

}

