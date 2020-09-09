import { Library } from './library';
import { SonarProject } from './SonarProject';
import { AuditTopic } from './AuditTopic';
import { ProjectSkill } from './project-skill';
import { Constants } from '../constants';

export class Project {

	public connectionSettings: number;
	public urlRepository: string;
	public username: string;
	public password: string;
	public filename: string;
	/**
	 * Branch name selected for the given repository.
	 * Default is master.
	 */
	public branch: string;

	/**
	 * This **boolean** indicates that this project has be involved in all analysis.
	 */
	public active = true;

	/**
	 * Staff risk evaluation to this project.
	 */
	public staffEvaluation: number;

	/**
	 * Global evaluation to this project after an audit given by experts.
	 */
	public auditEvaluation: number;

	/**
	 * This object is only used to retrieve the data from the server.
	 *
	 * Immediatly after the reception of data, the content will be transferred to **mapSkills**
	 */
	public skills: { [id: number]: ProjectSkill; } = {};

	/**
	 * Map containing the skills required for this project
	 */
	public mapSkills = new Map<number, ProjectSkill>();

	/**
	 * Array of dependeny paths (detected or declared) for this project.
	 */
	public libraries: Library[] = [];

	/**
	 * URL of the codefactor.io dashboard for your project.
	 */
	public urlCodeFactorIO;

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
		public id: number = Constants.UNKNOWN,
		public name: string = '') { }

}

