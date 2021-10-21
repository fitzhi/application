/**
 * The class represents a <span>&#x2605;</span> to be drawn
 * in the starfield component.
 */
export class Star {

	/**
	 * @param idSkill the skill identifier
	 * @param color the color of the star
	 * @param backgroundColor the background color of the HTML element containing this star
	 */
	constructor(
		public idSkill: number,
		public color?: string,
		public backgroundColor?: string) {}

}
