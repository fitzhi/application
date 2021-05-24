/**
 * This is a skill inside our company like Java; .Net, Tomcat, Angular JS, Angular TS
 */
export class SkillCountExperiences {
	public id: number;
	public title: string;
	count_1_star: number | string;
	count_2_star: number | string;
	count_3_star: number | string;
	count_4_star: number | string;
	count_5_star: number | string;

	/**
	 * @param id skill identifier.
	 * @param title title of the skill.
	 */
	constructor(id: number, title: string) {
		this.id = id;
		this.title = title;
	}
}
