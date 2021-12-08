import { Injectable } from '@angular/core';
import { Collaborator } from 'src/app/data/collaborator';
import { Experience } from 'src/app/data/experience';
import { Mission } from 'src/app/data/mission';
import { traceOn } from 'src/app/global';
import { StaffListService } from '../staff-list-service/staff-list.service';
import { TurnoverData } from './turnover-data';

/**
 * This service is in charge of the turnover calculation.
 */
@Injectable({
	providedIn: 'root'
})
export class TurnoverService {

	private fakeMissionMinFirstCommit = new Mission(8888, 'Fake project MIN');
	private fakeMissionMaxFirstCommit = new Mission(9999, 'Fake project MAX');

	constructor(private staffListService: StaffListService) { 
		this.fakeMissionMinFirstCommit.firstCommit = new Date(2064, 11, 31);
		this.fakeMissionMaxFirstCommit.lastCommit = new Date(1970, 0, 1);
	}

	/**
	 * Calculate the turnover for tha given year with the external developers included, or not.
	 * 
	 * The chosen calculation method to obtain the company turnover is 
	 * 
	 * @param year the given year
	 * @param external filter or not the external developers _If **TRUE**, the external developers should be included in the turnover calculation ?_
	 * @returns the turnoverDara container : the intermediate subtotals and the resulting turnover, which might be equal to -1, if calculation is impossible.
	 */
	public turnover (year: number, external = false): TurnoverData {

		const turnoverData = new TurnoverData();

		this.staffListService.allStaff
			.filter(staff => (!external) ? (!staff.external) : true )
			.forEach( staff => this.takeInAccount(staff, year, turnoverData));

		if (turnoverData.total === 0) {
			return TurnoverData.noDataAvailable();
		}

		// turnover = (resignation count + (arrival count / 2)) / number of workers at the 1st january
		turnoverData.calculation = Math.round( ((turnoverData.resignation + turnoverData.arrival) / 2) * 100 / turnoverData.total );
		if (traceOn()) {
			console.log ('Calculation of the turnover for year %d : ( ((%d + %d) / 2) * 100 / %d) = %d.', year, 
				turnoverData.resignation, turnoverData.arrival, turnoverData.total, 
				turnoverData.calculation);
		}
		return turnoverData;
	}

	private takeInAccount(staff: Collaborator, year: number, turnoverData: TurnoverData) {

		if (!(staff.missions) || (staff.missions.length == 0)) {
			return;
		}
		
		const firstExperience = staff.missions.reduce( (m1, m2) => (m1.firstCommit < m2.firstCommit) ? m1 : m2, this.fakeMissionMinFirstCommit);
		const lastExperience = staff.missions.reduce( (m1, m2) => (m1.lastCommit > m2.lastCommit) ? m1 : m2, this.fakeMissionMaxFirstCommit);

		// staff member to be exclude. Too deep in the past.
		if (lastExperience.lastCommit.getFullYear() < year) {
			return;
		}

		// staff member to be exclude. Too close from now.
		if (firstExperience.firstCommit.getFullYear() > year) {
			return;
		}

		// The first commit for this staff member has been submitted, after the 1st of january and before the 31st december of the given year.
		if ( (firstExperience.firstCommit.getFullYear() >= year) && (firstExperience.firstCommit.getFullYear() < (year + 1))) {
			turnoverData.arrival++
		}

		// The ultimate commit for this staff member has been submitted, after the 1st of january and before the 31st december of the given year.
		if (year === new Date(Date.now()).getFullYear()) {
			// For the actual year, the last commit for this staff member has been made in this year and the staff is flagged as inactive
			if ( (!staff.active) && (lastExperience.lastCommit.getFullYear() >= year) && (lastExperience.lastCommit.getFullYear() < (year + 1)))   {
				turnoverData.resignation++
			}
		} else {
			if ( (lastExperience.lastCommit.getFullYear() >= year) && (lastExperience.lastCommit.getFullYear() < (year + 1)) ) {
				turnoverData.resignation++
			}
		}

		// A staff member who has submited a commit before the 1st of january of the given year, and continues to do so during the year.
		if ( (firstExperience.firstCommit.getFullYear() < year) && (lastExperience.lastCommit.getFullYear() >= year ) ) {
			turnoverData.total++
		}

	}

}
