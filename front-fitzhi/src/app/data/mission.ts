
export class Mission {

	/**
	 * identifier of the project.
	 */
	idProject: number;

	/**
	 * Name of this project.
	 */
	name: string;

	/**
	 * First commit data for this staff member.
	 */
	firstCommit: Date;

	/**
	 * Last commit data for this staff member.
	 */
	lastCommit: Date;

	/**
	 * Number of commit submitted by the current developer inside this project.
	 */
	numberOfCommits: number;

	/**
	 * Number of commit submitted by the current developer inside this project.
	 */
	numberOfFiles: number;

	/**
	 * Constructor
	 */
	constructor(id: number, name: string) {
		this.idProject = id;
		this.name = name;
	}

}

