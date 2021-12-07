import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NOT_MODIFIED } from 'http-status-codes';
import { BehaviorSubject, EMPTY, Observable, of } from 'rxjs';
import { catchError, take, tap } from 'rxjs/operators';
import { Constants } from 'src/app/constants';
import { Experience } from 'src/app/data/experience';
import { Mission } from 'src/app/data/mission';
import { Collaborator } from '../../data/collaborator';
import { Commit } from '../../data/commit';
import { traceOn } from '../../global';
import { StaffService } from '../../tabs-staff/service/staff.service';
import { BackendSetupService } from '../backend-setup/backend-setup.service';
import { StaffListListenerService } from './staff-list-listener.service';


@Injectable({
	providedIn: 'root'
})
export class StaffListService {

	/**
	 * List of collaborators corresponding to the search criteria.
	 */
	public allStaff$ = new BehaviorSubject<Collaborator[]>([]);

	/**
	 * List of collaborators corresponding to the search criteria.
	 */
	public allStaff: Collaborator[] = [];

	/**
	 * This *boolean* represents the fact that the whole staff collection has been loaded;
	 */
	private allStaffLoaded = false;

	private allStaffLoadedSubject$ = new BehaviorSubject<boolean>(this.allStaffLoaded);
	/**
	 * This observable informs the application that the whole staff collection is loaded
	 * and available in the array allStaff[].
	 */
	public allStaffLoaded$ = this.allStaffLoadedSubject$.asObservable();

	/**
	 * Constructor.
	 * @param staffService service un charge of accessing the backend to retrive the staff members.
	 */
	constructor(
		private staffService: StaffService,
		private staffListListenerService: StaffListListenerService,
		private backendSetupService: BackendSetupService,
		private httpClient: HttpClient) {
	}

	/**
	 * Start the **Staff** loading process.
	 */
	startLoadingStaff(): void {
		this.reloadStaff();
		this.staffListListenerService.sub = this.staffListListenerService.intervalLoadStaff$.subscribe({
			next: val => {
				this.reloadStaff();
			}
		});
	}

	/**
	 * Load the staff members and store them in the array allStaff.
	 */
	reloadStaff() {
		if (traceOn()) {
			console.log(`Fetching the staff collection on URL ${this.backendSetupService.url()}/api/staff.`);
		}
		let headers = new HttpHeaders();
		if (this.getEtag()) {
			headers = headers.append('If-None-Match', this.getEtag());
		}

		this.httpClient
			.get<Collaborator[]>(`${this.backendSetupService.url()}/staff`,
				{
					headers: headers,
					responseType: 'json',
					observe: 'response'
				})
			.pipe(
				take(1),
				catchError(error => {
					// This is not an error, if the projects collection has not been mofified.
					// This is is the nominal behavior.
					if (error.status !== NOT_MODIFIED) {
						this.staffListListenerService.interruptStaffListener();
					}
					return EMPTY;
				})

			).subscribe({
				next: response => {
					this.saveEtag(response);
					this.takeInAccountStaff(response.body);
				}
			});
	}

	/**
	 * Take in account the staff list declared in the application.
	 * @param projects the retrieved staff members
	 */
	takeInAccountStaff(staff: Collaborator[]) {
		if (traceOn()) {
			console.groupCollapsed('Staff retrieved');
			staff.forEach(st => console.log(`${st.idStaff} ${st.firstName} ${st.lastName}`));
			console.table(staff);
			console.groupEnd();
		}
		this.loadAllStaff(staff);
		this.allStaff$.next(this.allStaff);
		if (!this.allStaffLoaded) {
			this.allStaffLoaded = true;
			this.informStaffLoaded();
		}
	}

	/**
	 * Load the collection of Staff members inside the Fitzhi framework.
	 * @param staff the staff object retrieved from the backend 
	 */
	public loadAllStaff (staff) {
		staff.forEach(staff => {
			const collaborator = this.createStaffFromJson(staff);
			this.allStaff.push(collaborator);
		});
	}

	private setStaffProperty (staff: Object, collaborator: Collaborator | Mission, property: string) {
		if (staff.hasOwnProperty(property)) {
			collaborator[property] = staff[property];
		}
	}

	private generateDate(dateReceived: string): Date {
		const str = dateReceived.split('-');
		const year = Number(str[0]);
		const month = Number(str[1]) - 1;
		const day = Number(str[2]);
	  	return new Date (year, month, day);
	}

	/**
	 * Create a staff member for the staff data retrieved from the backend.
	 * @param staff the loaded staff
	 * @returns the newly staff object
	 */
	createStaffFromJson(staff): Collaborator {

		const collaborator = new Collaborator();
		this.setStaffProperty(staff, collaborator, 'idStaff');
		this.setStaffProperty(staff, collaborator, 'firstName');
		this.setStaffProperty(staff, collaborator, 'lastName');

		this.setStaffProperty(staff, collaborator, 'nickName');
		this.setStaffProperty(staff, collaborator, 'login');
		this.setStaffProperty(staff, collaborator, 'email');

		this.setStaffProperty(staff, collaborator, 'level');
		this.setStaffProperty(staff, collaborator, 'forceActiveState');
		this.setStaffProperty(staff, collaborator, 'active');
		this.setStaffProperty(staff, collaborator, 'dateInactive');
		this.setStaffProperty(staff, collaborator, 'application');
		this.setStaffProperty(staff, collaborator, 'typeOfApplication');
		this.setStaffProperty(staff, collaborator, 'external');
		this.setStaffProperty(staff, collaborator, 'username');
		this.setStaffProperty(staff, collaborator, 'enabled');

		if (staff.hasOwnProperty('missions')) {
			collaborator.missions= [];
			staff.missions.forEach(element => {
				const m = new Mission(element.idProject, element.name);
				this.setStaffProperty(element, m, 'idStaff');
				if (element.hasOwnProperty('firstCommit')) {
					m.firstCommit = this.generateDate (element.firstCommit);
				}
				if (element.hasOwnProperty('lastCommit')) {
					m.lastCommit = this.generateDate (element.lastCommit);
				}
				this.setStaffProperty(element, m, 'numberOfCommits');
				this.setStaffProperty(element, m, 'numberOfFiles');

				collaborator.missions.push(m);
			});
		}

		if (staff.hasOwnProperty('experiences')) {
			collaborator.experiences = [];
			staff.experiences.forEach(element => {
				collaborator.experiences.push(new Experience(element.id, element.titme, element.level));
			});
		}

		return collaborator;
	}

	/**
	 * Save the `Etag` header in the session storage.
	 * @param response the response which contains the Etag header to be saved.
	 */
	saveEtag(response: HttpResponse<Collaborator[]>) {
		sessionStorage.setItem(Constants.ETAG_STAFF, response.headers.get('Etag'));
		if (traceOn()) {
			console.log('Etag saved', response.headers.get('Etag'));
		}
	}

	/**
	 * Get the `Etag` header from the session storage.
	 * @returns the Etag saved in the session storage
	 */
	private getEtag(): string {
		const etag = sessionStorage.getItem(Constants.ETAG_STAFF);
		if (traceOn()) {
			console.log('Etag retrieved', etag);
		}
		return etag;
	}


	/**
	 * Return the collaborator associated with this id in an observable.
	 */
	getCollaborator$(id: number): Observable<Collaborator> {

		let foundCollab: Collaborator = null;
		foundCollab = this.allStaff.find(collab => collab.idStaff === id);

		if (typeof foundCollab !== 'undefined') {
			// We create an observable for an element of the cache in order to be consistent with the direct reading.
			return of(foundCollab);
		} else {
			// The collaborator's id is not, or no more, available in the cache
			// We try a direct access
			if (traceOn()) {
				console.log('Direct access for : ' + id);
			}
			return this.staffService.get$(id).pipe(tap(
				(collab: Collaborator) => {
					if (traceOn()) {
						console.log('Direct access for : ' + id);
						if (typeof collab !== 'undefined') {
							console.log('Staff member found : ' + collab.firstName + ' ' + collab.lastName);
						} else {
							console.log('No staff found for id ' + id);
						}
					}
				}));
		}
	}

	/**
	 * Return the __collaborator__ associated with this id, or __null__ if none exists.
	 */
	public getCollaborator(id: number): Collaborator {
		const staff = this.allStaff.find(collab => collab.idStaff === id);
		if (!staff) {
			if (traceOn()) {
				console.log('Cannot retrieve the staff member with the identifier %d', id);
			}
			return null;
		} else {
			return staff;
		}
	}

	/**
	 * Update the staff collection with a new or update staff data filled in the form-staff component.
	 * @param staff the given staff
	 */
	setFormStaff(staff: Collaborator) {
		const collaborator = this.allStaff.find(collab => (staff.idStaff === collab.idStaff));
		if (!collaborator) {
			this.allStaff.push(staff);
			return;
		}
		collaborator.firstName = staff.firstName;
		collaborator.lastName = staff.lastName;
		collaborator.nickName = staff.nickName;
		collaborator.login = staff.login;
		collaborator.email = staff.email;
		collaborator.level = staff.level;
		collaborator.active = staff.active;
		collaborator.dateInactive = staff.dateInactive;
		collaborator.external = staff.external;
	}

	/**
	 * Return the list of staff members.
	 */
	getStaff(): Collaborator[] {
		return this.allStaff;
	}

	/**
	 * @param idProject the project identifier
	 * @returns the last registered commit.
	 */
	retrieveLastCommit(idProject: number): Commit {

		const latest = new Commit(-1, '', '', '1970-01-01');
		this.allStaff.forEach(staff => {
			staff.missions.forEach(mission => {
				if (mission.idProject === idProject) {
					if (mission.lastCommit) {
						if (new Date(mission.lastCommit) > new Date(latest.dateCommit)) {
							latest.idStaff = staff.idStaff;
							latest.firstName = staff.firstName;
							latest.lastName = staff.lastName;
							latest.dateCommit = mission.lastCommit.toString();
						}
					}
				}
			});
		});
		return latest;
	}

	/**
	 * Lookup for a staff member similar to the given one.
	 *
	 * This method is used to create twice the same developer
	 *
	 * @param staff a collaborator to retrieve with the actual team declared in the company.
	 */
	lookupSimilarStaff(staff: Collaborator): Collaborator {
		return this.allStaff
			.filter(collab => (collab.lastName))
			.find(collab =>
				(collab.firstName.replace('-', ' ').toLowerCase() === staff.firstName.toLowerCase()) &&
				(collab.lastName.replace('-', ' ').toLowerCase() === staff.lastName.toLowerCase()));
	}

	/**
	 * Return the index of the associated collaborator with the given id.
	 * @param idStaff the staff identifier
	 */
	findIndex(idStaff: number) {
		return this.allStaff.findIndex(staff => (staff.idStaff === idStaff));
	}

	/**
	 * Inform the system that the staff collection is loaded.
	 */
	informStaffLoaded() {
		this.allStaffLoadedSubject$.next(true);
	}
}
