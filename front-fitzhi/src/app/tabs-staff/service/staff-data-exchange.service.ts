import { Collaborator } from '../../data/collaborator';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { traceOn } from 'src/app/global';

@Injectable({
	providedIn: 'root'
})
export class StaffDataExchangeService {

	public collaboratorLoaded$ = new BehaviorSubject<boolean>(false);

	public collaborator: Collaborator;

	constructor() { }

	/**
	 * A new collaborator has been loaded and data has to be shared.
	 * @param collaborator the new collaborator loaded
	 */
	changeCollaborator(collaborator: Collaborator) {
		if (traceOn()) {
			console.log('collaborator switch from ' + ((this.collaborator) ? this.collaborator.lastName : 'empty') + ' to ' + collaborator.lastName);
		}
		this.collaborator = collaborator;
		this.collaboratorLoaded$.next(true);
	}

	/**
	 * Return **true** if this class is hosting an existing collaborator, and not an empty one.
	 */
	hostExistingCollaborator() {
		return ((this.collaborator) && (this.collaborator.idStaff > 0));
	}
}
