import { Component, OnInit, OnDestroy, Input, EventEmitter, Output } from '@angular/core';
import { FormControl, Validators, FormGroup, FormBuilder } from '@angular/forms';
import { BaseComponent } from '../../base/base.component';
import { MustMatch } from 'src/app/service/mustmatch';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { Constants } from 'src/app/constants';
import { MessageService } from 'src/app/interaction/message/message.service';
import { StaffDataExchangeService } from 'src/app/tabs-staff/service/staff-data-exchange.service';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { StaffService } from 'src/app/service/staff.service';
import { traceOn } from 'src/app/global';

@Component({
	selector: 'app-register-user',
	templateUrl: './register-user.component.html',
	styleUrls: ['./register-user.component.css']
})
export class RegisterUserComponent extends BaseComponent implements OnInit, OnDestroy {

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
		private formBuilder: FormBuilder,
		private staffService: StaffService,
		private backendSetupService: BackendSetupService,
		private staffDataExchangeService: StaffDataExchangeService,
		private messageBoxService: MessageBoxService,
		private messageService: MessageService) {
		super();
	}

	ngOnInit() {
		this.connectionGroup = this.formBuilder.group({
			username: new FormControl('', [Validators.required, Validators.maxLength(16)]),
			password: new FormControl('', [Validators.required, Validators.minLength(8), Validators.maxLength(16)]),
			passwordConfirmation: new FormControl('', [Validators.required, Validators.minLength(8), Validators.maxLength(16)]),
		}, {
				validator: MustMatch('password', 'passwordConfirmation')
			});
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
			.add(this.staffService.registerUser(
					this.veryFirstConnection,
					username,
					password)
				.subscribe(
					response => {
						if (response.code === 0) {
							if (traceOn()) {
								console.log('Empty staff created with id ' + response.staff.idStaff);
							}
							this.staffDataExchangeService.changeCollaborator(response.staff);
							this.messengerUserRegistered.emit(response.staff.idStaff);
						} else {
							this.messageService.error(response.message);
						}
					},
					error => {
						// We will restart the setup installation for the beginning
						this.backendSetupService.removeUrl();
						if (traceOn()) {
							console.log('Connection error ', error);
						}
					}));
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
		this.messengerSkipAndConnect.emit(true);
	}
}
