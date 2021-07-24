import { Contributor } from '../contributor';

/**
 * Object returned by the backend controller with the contributors of a project
 */
export class ProjectContributors {
	public idProject: number;
	public contributors: Contributor[];

}
