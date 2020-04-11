import { traceOn } from 'src/app/global';

export class SkillProjectsAggregation {
	constructor (
		public idSkill: string,
		public sumNumberOfFiles: number,
		public sumTotalFilesSize: number) {}

}
