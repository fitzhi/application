import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { Collaborator } from 'src/app/data/collaborator';
import { LoginEvent } from 'src/app/data/login-event';
import { LoginMode } from 'src/app/data/login-mode';
import { traceOn } from 'src/app/global';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { PasswordConfirmationMustMatchValidator } from 'src/app/service/password-confirmation-must-match-validator';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';

@Component({
	selector: 'app-register-user-form',
	templateUrl: './register-user-form.component.html',
	styleUrls: ['./register-user-form.component.css']
})
export class RegisterUserFormComponent extends BaseDirective implements OnInit, OnDestroy {

	/**
	 * Is this ever the first connection to this server, assuming that the user has to be "administrator" ?
	 */
	@Input() veryFirstConnection: boolean;

	/**
	 * We'll send to the parent component (RegisterUser) the new user has been created.
	 */
	@Output() messengerUserRegistered$ = new EventEmitter<LoginEvent>();

	/**
	 * Group of the components present in the form.
	 */
	public connectionGroup: FormGroup;

	constructor(
		private backendSetupService: BackendSetupService,
		private staffService: StaffService,
		private messageBoxService: MessageBoxService,
		private passwordConfirmationMatcher: PasswordConfirmationMustMatchValidator) {
			super();
		}

	ngOnInit(): void {
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
	public classOkButton() {
		return (this.connectionGroup.invalid) ? 'okButton okButtonInvalid' : 'okButton okButtonValid';
	}

	/**
	 * Save the user and password.
	 */
	public onSubmit() {

		const username: string = this.connectionGroup.get('username').value;
		const password: string = this.connectionGroup.get('password').value;
		if (traceOn()) {
			console.log( (this.veryFirstConnection ? 'Very first connection' : 'New user connection')
			+ ' Create new user for username/pass', username + '/' + password);
		}

		this.subscriptions
			.add(this.staffService.classicRegisterUser$(
					this.veryFirstConnection,
					username,
					password)
				.subscribe({
					next: (staff: Collaborator) => {
						traceOn() && console.log('Empty staff created with id ' + staff.idStaff);
						this.staffService.changeCollaborator(staff);
						this.messengerUserRegistered$.emit(new LoginEvent(staff.idStaff, LoginMode.CLASSIC));
					},
					error: error => {
						traceOn() && console.log('Connection error ', error);
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
						this.messengerUserRegistered$.emit(new LoginEvent(-1, LoginMode.CLASSIC));
					}
				}));
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

}
