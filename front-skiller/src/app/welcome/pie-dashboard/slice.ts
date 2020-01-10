import {TypeSlice} from './type-slice';

export class Slice {

	constructor(
		public id: number,
		public type: TypeSlice,
		public offset = 0,
		public angle: number,
		public color: string,
		public activated = false,
		public selected = false) {}
}
