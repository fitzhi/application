import { Skill } from './skill';
import { Library } from './library';
import { SonarProject } from './SonarProject';
import { AuditTopic } from './AuditTopic';

export class Project {

	public id: number;
	public name: string;
	public connectionSettings: number;
	public urlRepository: string;
	public username: string;
	public password: string;
	public filename: string;
	public risk: number;

	/**
	 * Array containing the skills required for this project
	 */
	public skills: Skill[] = [];

	/**
	 * Array of dependeny paths (detected or declared) for this project.
	 */
	public libraries: Library[] = [];

	/**
	 * Array containing the list of Sonar projects associated to this project.
	 */
	public sonarProjects: SonarProject[] = [];

	/**
	 * Map containing the list of topics.
	 * Key is the topic identifier.
	 */
	public audit: { [id: number]: AuditTopic; } = {};

	constructor() { }

}

