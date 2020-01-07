export class Slice {

	constructor(
		public id: number,
		public offset = 0,
		public angle: number,
		public color: string,
		public activated = false,
		public selcted = false) {}
}
