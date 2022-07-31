import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { traceOn } from 'src/app/global';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { AuthService } from '../../service/auth/auth.service';
import { environment } from '../../../../environments/environment';

@Component({
	selector: 'app-connect-user-form',
	templateUrl: './connect-user-form.component.html',
	styleUrls: ['./connect-user-form.component.css']
})
export class ConnectUserFormComponent implements OnInit {

	/**
	 * We'll send to the parent component (startingSetup) the new user is connected.
	 */
	@Output() messengerUserConnected$ = new EventEmitter<boolean>();

	/**
	 * Are we entering in this component, just by routing directly into '/login'
	 */
	@Input() private directLogin: boolean;

	/**
	 * Group of the components present in the form.
	 */
	public connectionGroup: FormGroup;

	constructor(private authService: AuthService,
		private projectService: ProjectService,
		private staffListService: StaffListService,
		private backendSetupService: BackendSetupService,
		private router: Router,
		private formBuilder: FormBuilder) {
		this.connectionGroup = this.formBuilder.group({
			username: new FormControl('', [Validators.required, Validators.maxLength(16)]),
			password: new FormControl('', [Validators.required, Validators.minLength(8), Validators.maxLength(16)])
		});
	}

	ngOnInit(): void {
		sessionStorage.clear();
		if (environment.autoConnect) {
			this.backendSetupService.saveUrl(environment.apiUrl);
			setTimeout(() => {
				traceOn() && console.log ('Auto connnection to Fitzhi.');
				// This is not a security leak. This is just an "anonymous" password
				this.connectionGroup.setValue({ username: 'guest', password: 'anonymous' }); // tslint:disable-line:comment-format //NOSONAR
				this.onSubmit();
			}, 1000);
		}
	}

		/**
	 * Class of the button corresponding to the 3 possible states of the "Ok" button.
	 */
	classOkButton() {
		return (this.connectionGroup.invalid) ?
			'okButton okButtonInvalid' : 'okButton okButtonValid';
	}

	/**
	 * Cancel the installation.
	 */
	onCancel() {
		traceOn() && console.log('onCancel');
		this.router.navigate(['/ciao']);
	}

	/**
	 * Submit the installation
	 */
	onSubmit() {
		const username: string = this.connectionGroup.get('username').value;
		const password: string = this.connectionGroup.get('password').value;
		this.authService.connectClassic$(username, password).subscribe({
			next: connectionStatus => {
				this.messengerUserConnected$.emit(connectionStatus);
				//
				// If the connection has succeeded, we load the projects and the staff members.
				//
				traceOn() && console.log ('Successful connection');
				if (connectionStatus) {
					// We load the projects and start the refresh process.
					this.projectService.startLoadingProjects();
					// We load the staff and start the refresh process.
					this.staffListService.startLoadingStaff();
				}

				if (this.directLogin) {
					traceOn() && console.log ('Redirecting to /welcome');
					this.router.navigate(['/welcome'], {});
				}
			}
		});
	}

	get username(): any {
		return this.connectionGroup.get('username');
	}

	get password(): any {
		return this.connectionGroup.get('password');
	}

}
