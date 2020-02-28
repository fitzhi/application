import { DetectionTemplate } from './detection-template';

/**
 * This is a skill inside our company like Java; .Net, Tomcat, Angular JS, Angular TS
 */
export class Skill {
	constructor (
		public id?: number,
		public title?: string,
		public detectionTemplate?: DetectionTemplate) {}
}
