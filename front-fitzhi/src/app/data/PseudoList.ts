import { Unknown } from './unknown';

export class PseudoList {

	/**
	 * identifier of the project.
	 */
	idProject: number;

	/**
	 * array of unknown pseudos.
	 */
	unknowns: Unknown[];

	/**
	 * Constructor
	 * @param idProject project identifier
	 * @param unknowns lists of unknown pseudos
	 */
	constructor(idProject: number, unknowns: Unknown[]) {
		this.idProject = idProject;
		this.unknowns = unknowns;
	}
}
