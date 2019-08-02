import { Injectable } from '@angular/core';
import { Contributor } from '../../data/contributor';
import { Constants } from '../../constants';

@Injectable({
	providedIn: 'root'
})
export class ProjectStaffService {

	/**
	 * List of contributors involved in a project.
	 */
	public contributors: Contributor[];

	public id = Constants.UNKNOWN;

	constructor() { }

	/**
	 * Return the NEXT staff's identifier from the project staff list.
	 */
	nextIdStaff(id: number): number {
		const index = this.contributors.findIndex(contributor => contributor.idStaff === id);
		if (Constants.DEBUG) {
			console.log('Current index : ' + index);
			console.log('Staff team collection size : ' + this.contributors.length);
		}

		if (index < this.contributors.length - 1) {
			return this.contributors[index + 1].idStaff;
		} else {
			return undefined;
		}
	}

	/**
	 * Return the PREVIOUS staff's identifier from the project staff list.
	 */
	previousIdStaff(id: number): number {
		const index = this.contributors.findIndex(contributor => contributor.idStaff === id);
		if (index > 0) {
			return this.contributors[index - 1].idStaff;
		} else {
			return undefined;
		}
	}

	displayLine(idx) {
		console.log(this.contributors[idx].idStaff + ' ' + this.contributors[idx].fullname);
	}
}
