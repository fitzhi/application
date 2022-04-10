import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, ViewChild } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';
import { doesNotReject } from 'assert';
import { of } from 'rxjs';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { FileService } from 'src/app/service/file.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { AuthService } from '../../service/auth/auth.service';
import { ConnectUserFormComponent } from './connect-user-form.component';


describe('ConnectUserFormComponent', () => {

	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	let authService: AuthService;
	let staffListService: StaffListService;
	let projectService: ProjectService;

	@Component({
		selector: 'app-host-component',
		template: `
			<div style="width: 600px; height: 300px; background-color: whiteSmoke; margin-top: 30px; margin-left: 100px; padding: 20px">
				<app-connect-user-form ($messengerUserConnected$)="onRegister($event)">
				</app-connect-user-form>
			</div>`})

	class TestHostComponent {

		@ViewChild(ConnectUserFormComponent) connectUserForm;

		constructor() {
		}
		onRegister($event) {
			console.log($event);
		}
	}

	function setUser(value: string) {
		const user = fixture.debugElement.query(By.css('#userConnection'));
		user.nativeElement.value = value;
		user.nativeElement.dispatchEvent(new Event('input'));
		fixture.detectChanges();
	}

	function setPassword(value: string) {
		const password = fixture.debugElement.query(By.css('#passwordConnection'));
		password.nativeElement.value = value; // This is not a credential. //NOSONAR
		password.nativeElement.dispatchEvent(new Event('input'));
		fixture.detectChanges();
	}

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ ConnectUserFormComponent, TestHostComponent ],
			providers: [AuthService, BackendSetupService, ProjectService, CinematicService, FileService, MessageBoxService, FormBuilder],
			imports: [HttpClientTestingModule, MatDialogModule, RouterTestingModule, FormsModule, ReactiveFormsModule]
		})
		.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;

		authService = TestBed.inject(AuthService);
		staffListService = TestBed.inject(StaffListService);
		projectService = TestBed.inject(ProjectService);

		fixture.detectChanges();
	});

	it('should be created with error.', () => {
		expect(component).toBeTruthy();
	});

	it('should have by default the sign-in button disabled.', () => {
		const btnOk = fixture.debugElement.query(By.css('#submitConnection'));
		expect(btnOk).toBeDefined();
		expect(btnOk.nativeElement.disabled).toBeTrue();
	});

	it('should NOT activate the signin button if the password does not contain 8 characters".', () => {
		setUser('frvidal');
		setPassword('1234567');
		fixture.detectChanges();
		const btnOk = fixture.debugElement.query(By.css('#submitConnection'));
		expect(btnOk).toBeDefined();
		expect(btnOk.nativeElement.disabled).toBeTrue();
	});

	it('should activate the signin button if the couple user/password is correctly filled".', () => {
		setUser('frvidal');
		setPassword('12345678');
		fixture.detectChanges();
		const btnOk = fixture.debugElement.query(By.css('#submitConnection'));
		expect(btnOk).toBeDefined();
		expect(btnOk.nativeElement.disabled).toBeFalse();
	});

	it('should process the connection request if end-user clicks on the "sign-in" button (successfull case).', () => {

		const spyConnect = spyOn(authService, 'connect$').and.returnValue(of(true));
		const spyStaffListService = spyOn(staffListService, 'startLoadingStaff').and.returnValue(null);
		const spyProjectService = spyOn(projectService, 'startLoadingProjects').and.returnValue(null);
		spyOn(component.connectUserForm.messengerUserConnected$, 'emit');

		setUser('frvidal');
		setPassword('12345678');
		fixture.detectChanges();

		const btnOk = fixture.debugElement.query(By.css('#submitConnection'));
		expect(btnOk).toBeDefined();
		expect(btnOk.nativeElement.disabled).toBeFalse();
		btnOk.nativeElement.click();
		fixture.detectChanges();

		expect(spyConnect).toHaveBeenCalled();
		expect(spyStaffListService).toHaveBeenCalled();
		expect(spyProjectService).toHaveBeenCalled();
		expect(component.connectUserForm.messengerUserConnected$.emit).toHaveBeenCalledWith(true);

	});

	it('should process the connection request (in error) if end-user clicks on the "sign-in" button (connection failure case).', () => {

		const spyConnect = spyOn(authService, 'connect$').and.returnValue(of(false));
		const spyStaffListService = spyOn(staffListService, 'startLoadingStaff').and.returnValue(null);
		const spyProjectService = spyOn(projectService, 'startLoadingProjects').and.returnValue(null);
		spyOn(component.connectUserForm.messengerUserConnected$, 'emit');

		setUser('frvidal');
		setPassword('12345678');
		fixture.detectChanges();

		const btnOk = fixture.debugElement.query(By.css('#submitConnection'));
		expect(btnOk).toBeDefined();
		expect(btnOk.nativeElement.disabled).toBeFalse();
		btnOk.nativeElement.click();
		fixture.detectChanges();

		expect(spyConnect).toHaveBeenCalled();
		expect(spyStaffListService).not.toHaveBeenCalled();
		expect(spyProjectService).not.toHaveBeenCalled();
		expect(component.connectUserForm.messengerUserConnected$.emit).toHaveBeenCalledWith(false);

	});

	it('should abort everything if end-user clicks on the "btnCancel" button).', () => {

		const spyConnect = spyOn(authService, 'connect$').and.returnValue(of(true));
		const spyStaffListService = spyOn(staffListService, 'startLoadingStaff').and.returnValue(null);
		const spyProjectService = spyOn(projectService, 'startLoadingProjects').and.returnValue(null);
		spyOn(component.connectUserForm.messengerUserConnected$, 'emit');

		const btnCancel = fixture.debugElement.query(By.css('#cancelConnection'));
		expect(btnCancel).toBeDefined();
		btnCancel.nativeElement.click();
		fixture.detectChanges();

		expect(spyConnect).not.toHaveBeenCalled();
		expect(spyStaffListService).not.toHaveBeenCalled();
		expect(spyProjectService).not.toHaveBeenCalled();
		expect(component.connectUserForm.messengerUserConnected$.emit).not.toHaveBeenCalledWith(true);

	});

});
