import { Contributor } from '../contributor';

export class ContributorsDTO {
	public code: number;
	public message: string;
	public idProject: number;
	public contributors: Contributor[];
}
