import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatInputModule } from '@angular/material/input';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { FileService } from 'src/app/service/file.service';
import { GoogleService } from 'src/app/service/google/google.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { RegisterUserFormComponent } from './register-user-form.component';


describe('RegisterUserFormComponent', () => {

	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let googleService: GoogleService;

	@Component({
		selector: 'app-host-component',
		template: `
			<div style="width: 600px; height: 300px; background-color: whiteSmoke; margin-top: 30px; margin-left: 100px; padding: 20px">
				<app-register-user-form></app-register-user-form>
			</div>`})

	class TestHostComponent {
		constructor() {}
	}

	function setUser(value: string) {
		const user = fixture.debugElement.query(By.css('#user'));
		user.nativeElement.value = value;
		user.nativeElement.dispatchEvent(new Event('input'));
		fixture.detectChanges();
	}

	function setPassword(value: string) {
		const password = fixture.debugElement.query(By.css('#password'));
		password.nativeElement.value = value; // This is not a credential. //NOSONAR
		password.nativeElement.dispatchEvent(new Event('input'));
		fixture.detectChanges();
	}

	function setConfirmedPassword(value: string) {
		const passwordConfirmation = fixture.debugElement.query(By.css('#passwordConfirmation'));
		passwordConfirmation.nativeElement.value = value; // This is not a credential. //NOSONAR
		passwordConfirmation.nativeElement.dispatchEvent(new Event('input'));
		fixture.detectChanges();
	}

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [RegisterUserFormComponent, TestHostComponent],
			providers: [ReferentialService, BackendSetupService, StaffService, FileService, MessageBoxService],
			imports: [FormsModule, MatGridListModule,
				HttpClientTestingModule, HttpClientModule, BrowserAnimationsModule, MatFormFieldModule,
				ReactiveFormsModule, MatInputModule, MatDialogModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;

		googleService = TestBed.inject(GoogleService);
		const spy = spyOn(googleService, 'initialize').and.returnValue();

		fixture.detectChanges();
	});

	it('should be created without error.', () => {
		expect(component).toBeDefined();
	});

	it('should NOT activate the button Ok, if the password length is lower than 8 characters.', () => {

		setUser('frvidal');
		setPassword('pass123');
		setConfirmedPassword('pass123');

		const btnOk = fixture.debugElement.query(By.css('#okButton'));
		expect(btnOk).toBeDefined();
		expect(btnOk.nativeElement.disabled).toBeTruthy();
	});

	it('should NOT activate the button Ok, if the password is not correctly confirmed.', () => {

		setUser('frvidal');
		setPassword('pass123word');
		setConfirmedPassword('pass123weird');

		const btnOk = fixture.debugElement.query(By.css('#okButton'));
		expect(btnOk).toBeDefined();
		expect(btnOk.nativeElement.disabled).toBeTruthy();
	});

	it('should activate the button Ok, if the user & password fields are correctly entered.', () => {

		setUser('frvidal');
		setPassword('pass123word');
		setConfirmedPassword('pass123word');

		const btnOk = fixture.debugElement.query(By.css('#okButton'));
		expect(btnOk).toBeDefined();
		expect(btnOk.nativeElement.disabled).toBeFalsy();

	});


});
