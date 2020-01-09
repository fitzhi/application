import { Injectable } from '@angular/core';
import { Project } from 'src/app/data/project';
import { Slice } from '../slice';
import { TypeSlice } from '../type-slice';

/**
 * This service is in charge of the generation of the slices.
 */
@Injectable({
	providedIn: 'root'
})
export class PieDashboardService {

	constructor() { }

	/**
	 * Generate the slices building the summary dashboard for an array of projects.
	 * @param projects array of projects (in reality, it will be an array with **ALL** projects)
	 */
	public generatePieSlices (projects: Project[]): Slice[] {
		const slices: Slice[] = [];
		slices.push(new Slice(0, TypeSlice.Sonar, 0, 120, 'green'));
		slices.push(new Slice(1, TypeSlice.Audit, 120, 120, 'orange'));
		slices.push(new Slice(2, TypeSlice.Staff, 240, 120, 'red'));
		return slices;
	}
}
