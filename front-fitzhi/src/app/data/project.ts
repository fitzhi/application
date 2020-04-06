import { Library } from './library';
import { SonarProject } from './SonarProject';
import { AuditTopic } from './AuditTopic';
import { ProjectSkill } from './project-skill';

export class Project {

	public connectionSettings: number;
	public urlRepository: string;
	public username: string;
	public password: string;
	public filename: string;

	/**
	 * Staff risk evaluation to this project.
	 */
	public staffEvaluation: number;

	/**
	 * Global evaluation to this project after an audit given by experts.
	 */
	public auditEvaluation: number;

	/**
	 * Array containing the skills required for this project
	 */
	public skills: ProjectSkill[] = [];

	/**
	 * Array of dependeny paths (detected or declared) for this project.
	 */
	public libraries: Library[] = [];

	/**
	 * URL of the Sonar server used to validate this project.
	 */
	public urlSonarServer;

	/**
	 * Array containing the list of Sonar projects associated to this project.
	 */
	public sonarProjects: SonarProject[] = [];

	/**
	 * Map containing the list of topics.
	 * Key is the topic identifier.
	 */
	public audit: { [id: number]: AuditTopic; } = {};

	/**
	 * Array of ecosystem identifiers detected in this project.
	 */
	public ecosystems: number[] = [];

	constructor(
		public id: number = 0,
		public name: string = '') { }

}

