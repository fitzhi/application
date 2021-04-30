import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Collaborator } from '../data/collaborator';
import { Constants } from '../constants';

import { StaffListService } from '../service/staff-list-service/staff-list.service';
import { StaffDataExchangeService } from './service/staff-data-exchange.service';
import { CinematicService } from '../service/cinematic.service';
import { MessageService } from '../interaction/message/message.service';
import { BaseComponent } from '../base/base.component';
import { Subject, BehaviorSubject } from 'rxjs';
import { traceOn } from '../global';
import { StaffService } from './service/staff.service';

@Component({
	selector: 'app-staff',
	templateUrl: './staff.component.html',
	styleUrls: ['./staff.component.css']
})
export class StaffComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * Staff member identifier shared with the child components (staffTabs, StaffForm)
	 */
	public idStaff: number;

	private TAB_FORM = 0;
	private TAB_ = 1;

	private collaborator: Collaborator;

	/**
	 * Selected TAB. This observable is fired by the StaffComponent class when the user changes the tab selected.
	 */
	public selectedTab$ = new Subject();

	constructor(
		public cinematicService: CinematicService,
		private route: ActivatedRoute,
		public staffListService: StaffListService,
		public messageService: MessageService,
		public staffDataExchangeService: StaffDataExchangeService,
		public staffService: StaffService,
		private router: Router) {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(this.route.params.subscribe(params => {
			if (traceOn()) {
				console.log('params[\'id\'] ' + params['id']);
			}
			if (params['id'] == null) {
				this.idStaff = null;
			} else {
				this.idStaff = + params['id']; // (+) converts string 'id' to a number
			}

			// Either we are in creation mode, or we load the collaborator from the back-end...
			// We create an empty collaborator until the subscription is complete
			this.collaborator = this.staffService.emptyStaff();

			this.staffDataExchangeService.changeCollaborator(this.collaborator);

			/*
			 * By default, you cannot add a project/skill for an unregistered developer.
			 */
			document.querySelector('body').style.cssText = '--actions-button-visible: hidden';
			if (this.idStaff != null) {
				this.staffListService.getCollaborator$(this.idStaff).subscribe(
					(collab: Collaborator) => {
						this.staffDataExchangeService.changeCollaborator(collab);
						this.collaborator = collab;
						if (collab.active) {
							document.querySelector('body').style.cssText = '--actions-button-visible: visible';
						} else {
							document.querySelector('body').style.cssText = '--actions-button-visible: hidden';
						}
						this.cinematicService.emitActualCollaboratorDisplay.next(this.collaborator.idStaff);
						this.cinematicService.setForm(Constants.DEVELOPERS_CRUD, this.router.url);
					},
					error => {
						if (error.status === 404) {
							if (traceOn()) {
								console.log('404 : cannot found a collaborator for the id ' + this.idStaff);
							}
							this.messageService.error('There is no staff member for id ' + this.idStaff);
							this.collaborator = {
								idStaff: -1, firstName: null, lastName: null, nickName: null, login: null, email: null, level: null,
								forceActiveState: false, active: true, dateInactive: null, application: null, typeOfApplication: null, external: false,
								missions: [], experiences: []
							};
						} else {
							console.error(error);
						}
					},
					() => {
						if (this.collaborator.idStaff === 0) {
							console.log('No collaborator found for the id ' + this.idStaff);
						}
						if (traceOn()) {
							console.log('Loading complete for id ' + this.idStaff, this.collaborator);
						}
					}
				);
			}
		}));
		this.cinematicService.setForm(Constants.DEVELOPERS_CRUD, this.router.url);
	}

	/**
	 * This method is fired by the <mat-tab-group..> container when the selected tab is changed.
	 * @param selectedIndex the selected tab index
	 */
	selectedIndexChange(selectedIndex: number) {
		if (traceOn()) {
			console.log ('Index of selected tab', selectedIndex);
		}
		this.selectedTab$.next(selectedIndex);
		switch (selectedIndex) {
			case 0:
				this.cinematicService.setForm(Constants.DEVELOPERS_CRUD, this.router.url);
				break;
		}
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}


