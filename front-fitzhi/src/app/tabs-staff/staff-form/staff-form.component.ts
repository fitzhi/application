import { Constants } from '../../constants';
import { Collaborator } from '../../data/collaborator';
import { MessageService } from '../../message/message.service';
import { StaffService } from '../../service/staff.service';
import { CinematicService } from '../../service/cinematic.service';
import { Profile } from '../../data/profile';
import { ReferentialService } from '../../service/referential.service';
import { StaffDataExchangeService } from '../service/staff-data-exchange.service';

import { Component, OnInit, Input, OnDestroy, Output, EventEmitter } from '@angular/core';

import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { BaseComponent } from '../../base/base.component';
import { TabsStaffListService } from 'src/app/tabs-staff-list/service/tabs-staff-list.service';
import { take } from 'rxjs/operators';
import { MessageBoxService } from 'src/app/message-box/service/message-box.service';
import { Mission } from 'src/app/data/mission';
import { StaffListService } from 'src/app/staff-list-service/staff-list.service';
import { traceOn } from 'src/app/global';


@Component({
	selector: 'app-staff-form',
	templateUrl: './staff-form.component.html',
	styleUrls: ['./staff-form.component.css']
})
export class StaffFormComponent extends BaseComponent implements OnInit, OnDestroy {

	@Input() idStaff: number;

	/**
	 * Selected TAB.
	 * This observable is fired by the StaffComponent class when the user changes the tab selected.
	 */
	@Input() selectedTab$;

	FIRST_NAME = 1;
	LAST_NAME = 2;
	IS_ACTIVE = 3;

	/**
     * This messenger is there to be used in one case : if the parent component is the INSTALLATION SETUP.
     * The staff member has been updated during the first setup of INSTALLATION.
     * We can continue to next step.
     */
	@Output() messengerStaffUpdated = new EventEmitter<Collaborator>();

	/**
	 * Inform the parent component, e.g. the staff component that we must switch the context to an empty collaborator.
	 */
	@Output() messengerInit = new EventEmitter<Boolean>();


	private collaborator: Collaborator;

	/**
     * Label on side of the active check box.
     */
	label_isActive: string;
	label_dateInactive: Date;

	/**
     * list of profiles used on the SELECT field.
     */
	profiles: Profile[];

	public profileStaff = new FormGroup({
		firstName: new FormControl('', [Validators.maxLength(16), this.noUselessWhitespaceValidator]),
		lastName: new FormControl('', [Validators.required, Validators.maxLength(16), this.noUselessWhitespaceValidator]),
		nickName: new FormControl('', [Validators.maxLength(16), this.noUselessWhitespaceValidator]),
		login: new FormControl('', [Validators.required, Validators.maxLength(16), this.noUselessWhitespaceValidator]),
		email: new FormControl('', [Validators.required, Validators.maxLength(32), Validators.email]),
		profile: new FormControl(null, [Validators.required]),
		active: new FormControl(1),
		external: new FormControl(0)
	});

	public noUselessWhitespaceValidator(control: FormControl) {
		if (!control.value) {
			return null;
		}
		const hasUselessWhitespace = (control.value.length !== control.value.trim().length);
		return !hasUselessWhitespace ? null : { 'whitespace': true };
	}

	constructor(
		private staffService: StaffService,
		private messageService: MessageService,
		private messageBoxService: MessageBoxService,
		private cinematicService: CinematicService,
		private referentialService: ReferentialService,
		private staffDataExchangeService: StaffDataExchangeService,
		private tabsStaffListService: TabsStaffListService,
		private staffListService: StaffListService,
		private router: Router) {
		super();
	}

	ngOnInit() {

		if (traceOn()) {
			console.log('Current staff member id ' + this.idStaff);
		}
		/**
		 * We listen the parent component (StaffComponent) in charge of retrieving data from the back-end.
		 */
		this.subscriptions.add(
			this.staffDataExchangeService.collaboratorLoaded$.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						this.takeInAccountCollaborator(this.staffDataExchangeService.collaborator);
					} else {
						this.initStaff();
					}
				}
			}));

		this.profiles = this.referentialService.profiles;
	}

	/*
	 * Taking in account a collaborator loaded from the server.
	 * @param collaborator the current active collaborator.
	 */
	takeInAccountCollaborator (collaborator: Collaborator) {
		if (traceOn()) {
			console.log('Employee loaded ' + collaborator.idStaff);
		}
		this.collaborator = collaborator;
		this.profileStaff.get('firstName').setValue(this.collaborator.firstName);
		this.profileStaff.get('lastName').setValue(this.collaborator.lastName);
		this.profileStaff.get('nickName').setValue(this.collaborator.nickName);
		this.profileStaff.get('login').setValue(this.collaborator.login);
		this.profileStaff.get('email').setValue(this.collaborator.email);
		this.profileStaff.get('profile').setValue(this.collaborator.level);
		this.profileStaff.get('active').setValue(this.collaborator.active);
		if (this.collaborator.active) {
			if (this.collaborator.idStaff === null) {
				this.label_isActive = 'will be considered in activity as long as this box is checked ';
			} else {
				if (this.collaborator.lastName === null) {
					this.label_isActive =
						'This collaborator is still in activity. Uncheck this box to inform of his leave.';
				} else {
					this.label_isActive =
						((this.collaborator.firstName === null) ? '' : this.collaborator.firstName)
						+ ' ' + this.collaborator.lastName
						+ ' is still in activity. Uncheck this box to inform of his leave.';
				}
			}
			// There is no READONLY attribute in the SELECT widget.
			// We need to enable this field within the code and not in HTML like the rest of the form.
			this.profileStaff.get('profile').enable();
			this.profileStaff.get('external').enable();
		} else {
			this.label_isActive = this.collaborator.firstName + ' ' +
				this.collaborator.lastName + ' in no more in activity since ';
			this.label_dateInactive = this.collaborator.dateInactive;
			// There is no READONLY attribute in the SELECT widget.
			// We need to disable this field within the code and not in HTML like the rest of the form.
			this.profileStaff.get('profile').disable();
			this.profileStaff.get('external').disable();
		}
		this.profileStaff.get('external').setValue(this.collaborator.external);
		this.cinematicService.setForm(Constants.DEVELOPERS_CRUD, this.router.url);
	}

	/**
	 * Initialization of the collaborator inside this form.
	 */
	initStaff() {
		this.collaborator = {
			idStaff: -1, firstName: '', lastName: '', nickName: '', login: '', email: '', level: '',
			dateInactive: null, application: null, typeOfApplication: null, active: true, external: true,
			experiences: [], missions: []
		};
		this.profileStaff.get('firstName').setValue('');
		this.profileStaff.get('lastName').setValue('');
		this.profileStaff.get('nickName').setValue('');
		this.profileStaff.get('login').setValue('');
		this.profileStaff.get('email').setValue('');
		this.profileStaff.get('profile').setValue('');
		this.profileStaff.get('active').setValue(true);
		this.label_isActive = 'will be considered in activity as long as this box is checked ';
		this.profileStaff.get('external').setValue(false);
	}

	/**
    * The Submit Button has been activated
    */
	onSubmit(): void {
		if (traceOn()) {
			console.log('Saving data for the collaborator below');
			console.log(this.collaborator);
		}
		this.collaborator.firstName = this.profileStaff.get('firstName').value;
		this.collaborator.lastName = this.profileStaff.get('lastName').value;
		this.collaborator.nickName = this.profileStaff.get('nickName').value;
		this.collaborator.login = this.profileStaff.get('login').value;
		this.collaborator.email = this.profileStaff.get('email').value;
		this.collaborator.level = this.profileStaff.get('profile').value;
		this.collaborator.active = this.profileStaff.get('active').value;
		this.collaborator.external = this.profileStaff.get('external').value;

		this.staffService.save(this.collaborator)
			.pipe(take(1))
			.subscribe(
				staff => {
					this.collaborator = staff;
					this.messengerStaffUpdated.emit(staff);

					//
					// If this staff member exists in pre-existing list of collaborators. We actualize the content.
					//
					this.tabsStaffListService.actualizeCollaborator(staff);
					this.staffDataExchangeService.changeCollaborator(staff);

					this.staffListService.setFormStaff(staff);

					this.messageService.success('Staff member ' + this.collaborator.firstName + ' ' + this.collaborator.lastName + ' saved');
				});
	}

	/**
     * Test if the collaborator has been already deactivated on the database.
     * You can test this state by testing the dateInactive, filled by the back-end during the deactivation process.
     */
	public isAlreadyDesactivated(): boolean {
		return (!this.collaborator.active);
	}

	/**
     * Class of the button corresponding to the 3 possible states of the "Ok" button.
     */
	classOkButton() {
		if (this.isAlreadyDesactivated()) {
			return (this.collaborator.active) ?
				'okButton okButtonValid' : 'okButton okButtonInvalid';
		}
		return (this.profileStaff.invalid) ?
			'okButton okButtonInvalid' : 'okButton okButtonValid';
	}

	/**
	 * Content of a field has been updated.
	 * @param field field identified throwing this event.
	 */
	public onChange(field: number) {

		//
		// We remove uselss blank, which might disturb the lookup feature.
		//
		if (this.collaborator.idStaff === -1) {
			return;
		}

		const newFirstName = this.profileStaff.get('firstName').value;
		const oldFirstName = this.collaborator.firstName;
		const newLastName = this.profileStaff.get('lastName').value;
		const oldLastName = this.collaborator.lastName;

		// The staff member was desactivated. And the user wants to reactivate him.
		if (field === this.IS_ACTIVE) {
			if (this.profileStaff.get('active').value) {
				if (traceOn()) {
					console.log ('The end-user wants to reactivate %s %s', oldFirstName, oldLastName);
				}
				this.collaborator.active = true;
				this.profileStaff.get('active').setValue(true);
				this.collaborator.dateInactive = null;
			}
			return;
		}

		if (traceOn()) {
			console.groupCollapsed('Staff member is moving');
			console.log ('Field', field);
			console.log ('Firstname', newFirstName + '->' + oldFirstName);
			console.log ('Lastname', newLastName + '->' + oldLastName);
			console.groupEnd();
		}

		//
		// Some staff might have been created only with their login.
		// The first user in the application is a godd example
		// So we last test is not necessary if the first & the last name are empty.
		//
		if ((!oldFirstName) && (!oldLastName)) {
			return;
		}

		if ( (newFirstName !== oldFirstName) && (newLastName !== oldLastName) ) {
			this.messageBoxService.question(
				'Staff Form',
				'You have already changed the first name and the last name of ' + oldFirstName + ' ' + oldLastName + '.'
				+ '<br/>Maybe you do not want to change this record and just prefer to create a new one.<br/>'
				+ '<br/>Click \'Yes\' if you want to create a new staff member'
				+ '<br/>\'No\' to continue updating this one.')
				.pipe(take(1))
				.subscribe(answer => {
					if (answer) {
						this.initStaff();
						this.collaborator.firstName = newFirstName;
						this.collaborator.lastName = newLastName;
						this.staffDataExchangeService.changeCollaborator(this.collaborator);
					}
				});
		}
	}

	/**
	 * Evaluate if the current collaborator is registered as active inside the Zhistem.
	 * @returns TRUE if the collaborator has an active mission.
	 */
	hasBeenActive(): boolean {
		const activeMission = this.collaborator.missions
			.find(mission => mission.lastCommit);
		return (activeMission !== undefined);
	}

	/**
	 * @returns the last mission executed by this collaborator, as declared in his mission list.
	 */
	lastMission(): Mission {
		const missions = this.collaborator.missions
			.filter(mission => mission.lastCommit)
			.sort( (mission1, mission2) => {
				return (new Date(mission2.lastCommit).getTime() - new Date(mission1.lastCommit).getTime());
			});
		return missions[0];
	}

	get firstName(): any {
		return this.profileStaff.get('firstName');
	}

	get lastName(): any {
		return this.profileStaff.get('lastName');
	}

	get nickName(): any {
		return this.profileStaff.get('nickName');
	}

	get login(): any {
		return this.profileStaff.get('login');
	}

	get profile(): any {
		return this.profileStaff.get('profile');
	}

	get email(): any {
		return this.profileStaff.get('email');
	}

	get external(): any {
		return this.profileStaff.get('external');
	}

	/**
     * Calling the base class to unsubscribe all subscriptions.
     */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
