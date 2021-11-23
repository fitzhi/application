import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Collaborator } from 'src/app/data/collaborator';
import { traceOn } from 'src/app/global';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { PasswordConfirmationMustMatchValidator } from 'src/app/service/password-confirmation-must-match-validator';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { BaseDirective } from '../../base/base-directive.directive';
import { InstallService } from '../service/install/install.service';

@Component({
	selector: 'app-register-user',
	templateUrl: './register-user.component.html',
	styleUrls: ['./register-user.component.css']
})
export class RegisterUserComponent extends BaseDirective implements OnInit, OnDestroy {

	/**
     * We'll send to the parent component (startingSetup) the new user has been created.
     */
	@Output() messengerUserRegistered = new EventEmitter<number>();

	/**
     * We'll send to the parent component (startingSetup) the new user has been created.
     */
	@Output() messengerSkipAndConnect = new EventEmitter<boolean>();

	/**
     * Is this ever the first connection to this server, assuming that the user has to be "administrator" ?
     */
	@Input() veryFirstConnection: boolean;

	/**
     * Group of the components present in the form.
     */
	public connectionGroup: FormGroup;

	constructor(
		private staffService: StaffService,
		private backendSetupService: BackendSetupService,
		private messageBoxService: MessageBoxService,
		private installService: InstallService,
		private passwordConfirmationMatcher: PasswordConfirmationMustMatchValidator) {
		super();
	}

	ngOnInit() {
		this.connectionGroup = new FormGroup(
			{
				username: new FormControl('', [Validators.required, Validators.maxLength(16)]),
				password: new FormControl('', [Validators.required, Validators.minLength(8), Validators.maxLength(16)]),
				passwordConfirmation: new FormControl('', [Validators.required, Validators.minLength(8), Validators.maxLength(16)]),
			},
			this.passwordConfirmationMatcher.check()
		);
	}

	/**
     * Class of the button corresponding to the 3 possible states of the "Ok" button.
     */
	classOkButton() {
		return (this.connectionGroup.invalid) ?
			'okButton okButtonInvalid' : 'okButton okButtonValid';
	}

	get username(): any {
		return this.connectionGroup.get('username');
	}

	get password(): any {
		return this.connectionGroup.get('password');
	}

	get passwordConfirmation(): any {
		return this.connectionGroup.get('passwordConfirmation');
	}

	/**
     * Calling the base class to unsubscribe all subscriptions.
     */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

	/**
     * Save the user and password.
     */
	onSubmit() {

		const username: string = this.connectionGroup.get('username').value;
		const password: string = this.connectionGroup.get('password').value;
		if (traceOn()) {
			console.log( (this.veryFirstConnection ? 'Very first connection' : 'New user connection')
			+ ' Create new user for username/pass', username + '/' + password);
		}

		this.subscriptions
			.add(this.staffService.registerUser$(
					this.veryFirstConnection,
					username,
					password)
				.subscribe({
					next: (staff: Collaborator) => {
						if (traceOn()) {
							console.log('Empty staff created with id ' + staff.idStaff);
						}
						this.staffService.changeCollaborator(staff);
						this.messengerUserRegistered.emit(staff.idStaff);
					},
					error: error => {
						if (traceOn()) {
							console.log('Connection error ', error);
						}
					}
				})
			);
	}

	/**
     * Cancel the installation
     */
	onCancel() {
		this.subscriptions.add(
			this.messageBoxService.question(
				'Cancel of operation',
				'Do you confim the cancellation ?')
				.subscribe(answer => {
					if (answer) {
						this.backendSetupService.removeUrl();
						this.messengerUserRegistered.emit(-1);
					}
				}));
	}

	/**
	 * Skip the registration of a new user.
	 */
	public skip() {
		// We do know at this point the staff identifier corresponding to this user.
		this.installService.installComplete();
		this.messengerSkipAndConnect.emit(true);
	}
}
