import { Injectable } from '@angular/core';
import { Contributor } from '../../data/contributor';
import { Constants } from '../../constants';
import { traceOn } from 'src/app/global';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';

@Injectable({
	providedIn: 'root'
})
export class ProjectStaffService {

	/**
	 * List of contributors involved in a project.
	 */
	public contributors: Contributor[] = [];

	public id = Constants.UNKNOWN;

	constructor(private staffListService: StaffListService) { }

	public dumpContributors(): void {
		console.groupCollapsed('Project contributors retrieved');
		console.log(...this.contributors);
		console.groupEnd();
	}

	/**
	 * Return the NEXT staff's identifier from the project staff list.
	 */
	nextIdStaff(id: number): number {
		const index = this.contributors.findIndex(contributor => contributor.idStaff === id);
		if (traceOn()) {
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

	/**
	 * Search for a contributor with the same identifier as the given one
	 * @param idStaff the searched staff identifier
	 */
	findContributor(idStaff: number): Contributor {
		const foundContributor = this.contributors
			.find(contributor => contributor.idStaff === idStaff);
		if (!foundContributor) {
			if (traceOn()) {
				console.log (`Conmmiter\'s id ${idStaff} is not retrieved in the staff team.`);
			}
			const unknown = new Contributor();
			unknown.idStaff = idStaff;
			const staff = this.staffListService.getCollaborator(idStaff);
			if (staff) {
				unknown.fullname = staff.firstName + ' ' + staff.lastName;
				unknown.active = staff.active;
				unknown.external = staff.external;
			} else {
				unknown.fullname = 'Unknown ' + idStaff;
				unknown.active = false;
			}
			return unknown;
		}
		return foundContributor;
	}



}
