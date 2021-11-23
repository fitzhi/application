/**
 * The class represents a constellation of skills levels.
 * A constellation is a group of <span>&#x2605;</span> to be drawn
 * in the starfield component.
 */
export class Constellation {

	/**
	 * @param idSkill the skill identifier
	 * @param count The number of stars counted for this constallation
	 * @param color the color of each star
	 * @param backgroundColor the background color of the HTML element containing each star
	 */
	constructor(
		public idSkill: number,
		public count: number,
		public color?: string,
		public backgroundColor?: string) {}

}
