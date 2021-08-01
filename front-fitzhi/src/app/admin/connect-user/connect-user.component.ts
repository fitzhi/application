import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { traceOn } from 'src/app/global';
import { ProjectService } from 'src/app/service/project/project.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { AuthService } from '../service/auth/auth.service';

@Component({
	selector: 'app-connect-user',
	templateUrl: './connect-user.component.html',
	styleUrls: ['./connect-user.component.css']
})
export class ConnectUserComponent implements OnInit {

	/**
     * We'll send to the parent component (startingSetup) the new user is connected.
     */
	@Output() messengerUserConnected = new EventEmitter<boolean>();

	/**
	 * Are we entering in this component, just by routing directly into '/login'
	 */
	@Input() private directLogin = true;

	/**
     * Group of the components present in the form.
     */
	public connectionGroup: FormGroup;

	constructor(
		private authService: AuthService,
		private projectService: ProjectService,
		private staffListService: StaffListService,
		private router: Router,
		private formBuilder: FormBuilder) {
		this.connectionGroup = this.formBuilder.group({
			username: new FormControl('', [Validators.required, Validators.maxLength(16)]),
			password: new FormControl('', [Validators.required, Validators.minLength(6), Validators.maxLength(16)])
		});
	}

	ngOnInit() {
		sessionStorage.clear();
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
		if (traceOn()) {
			console.log('onCancel');
		}
		this.router.navigate(['/ciao']);
	}

	/**
     * Cancel the installation
     */
	onSubmit() {
		const username: string = this.connectionGroup.get('username').value;
		const password: string = this.connectionGroup.get('password').value;
		this.authService.connect$(username, password).subscribe({

			next: connectionStatus => {
				this.messengerUserConnected.emit(connectionStatus);
				/**
				 * If the connection has succeeded, we load the projects and the staff members.
				 */
				if (connectionStatus) {
					// We load the projects and start the refresh process.
					this.projectService.startLoadingProjects();
					this.staffListService.loadStaff();
				}

				if (this.directLogin) {
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
