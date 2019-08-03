import { Skill } from './skill';
import { Dependency } from './dependency';

export class Project {

	public id: number;
	public name: string;
	public connectionSettings: number;
	public urlRepository: string;
	public username: string;
	public password: string;
	public filename: string;
	public skills: Skill[];

	/**
	 * Array of dependeny paths (detected or declared) for this project.
	 */
	public dependencies: Dependency[];

	constructor() { }

}

