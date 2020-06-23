import {TypeSlice} from './type-slice';
import { Project } from 'src/app/data/project';

export class Slice {

	constructor(
		public id: number,
		public type: TypeSlice,
		public offset = 0,
		public angle: number,
		public levelStaffRisk: number,
		public color: string,
		public projects: Project[],
		public activated = false,
		public selected = false) {}
}
