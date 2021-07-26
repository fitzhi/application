import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RegisterUserComponent } from './register-user.component';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ReferentialService } from 'src/app/service/referential.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatGridListModule } from '@angular/material/grid-list';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSliderModule } from '@angular/material/slider';
import { MatInputModule } from '@angular/material/input';
import { MatDialogModule } from '@angular/material/dialog';
import { By } from '@angular/platform-browser';
import { InstallService } from '../service/install/install.service';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { Collaborator } from 'src/app/data/collaborator';
import { of } from 'rxjs';


describe('RegisterUserComponent', () => {
	let component: RegisterUserComponent;
	let fixture: ComponentFixture<RegisterUserComponent>;

	let httpClient: HttpClient;
	let httpTestingController: HttpTestingController;

	let installService: InstallService;

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [RegisterUserComponent],
			providers: [ReferentialService, CinematicService, InstallService],
			imports: [MatCheckboxModule, MatTableModule, FormsModule, MatPaginatorModule, MatGridListModule,
				HttpClientTestingModule, HttpClientModule, BrowserAnimationsModule, MatFormFieldModule,
				ReactiveFormsModule, MatSliderModule, MatInputModule, MatDialogModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		// Inject the http service and test controller for each test
		httpClient = TestBed.inject(HttpClient);
		httpTestingController = TestBed.inject(HttpTestingController);

		fixture = TestBed.createComponent(RegisterUserComponent);
		component = fixture.componentInstance;

		installService = TestBed.inject(InstallService);

		fixture.detectChanges();
	});

	it('should create with the buttons active, or inactive.', () => {
		expect(component).toBeTruthy();

		const btnConnect = fixture.debugElement.query(By.css('#btn-connect'));
		expect(btnConnect).toBeDefined();
		expect(btnConnect.nativeElement.disabled).toBeFalsy();

		const btnOk = fixture.debugElement.query(By.css('#okButton'));
		expect(btnOk).toBeDefined();
		expect(btnOk.nativeElement.disabled).toBeTruthy();

		const btnCancel = fixture.debugElement.query(By.css('#cancelButton'));
		expect(btnCancel).toBeDefined();
		expect(btnCancel.nativeElement.disabled).toBeFalsy();

		fixture.detectChanges();
	});

	it('Should complete installation if the end-user clicks on skip.', () => {

		const spy = spyOn(installService, 'installComplete').and.callThrough();

		const btnConnect = fixture.debugElement.query(By.css('#btn-connect'));
		expect(btnConnect).toBeDefined();
		btnConnect.nativeElement.click();
		fixture.detectChanges();

		expect(localStorage.getItem('installation')).toBe('1');
	});

	it('Should activate the button Ok, if the user & password fields are correctly entered.', () => {

		const spy = spyOn(installService, 'installComplete').and.callThrough();

		const user = fixture.debugElement.query(By.css('#user'));
		user.nativeElement.value = 'frvidal';
		user.nativeElement.dispatchEvent(new Event('input'));
		fixture.detectChanges();

		const password = fixture.debugElement.query(By.css('#password'));
		password.nativeElement.value = 'pass123word'; // This is not a credential. //NOSONAR
		password.nativeElement.dispatchEvent(new Event('input'));
		fixture.detectChanges();

		const passwordConfirmation = fixture.debugElement.query(By.css('#passwordConfirmation'));
		passwordConfirmation.nativeElement.value = 'pass123word'; // This is not a credential. //NOSONAR
		passwordConfirmation.nativeElement.dispatchEvent(new Event('input'));
		fixture.detectChanges();

		const btnOk = fixture.debugElement.query(By.css('#okButton'));
		expect(btnOk).toBeDefined();
		expect(btnOk.nativeElement.disabled).toBeFalsy();

	});

	it('Should handle correctly the registration of a new user.', () => {

		const staffService = TestBed.inject(StaffService);
		const spyChangeCollaborator = spyOn(staffService, 'changeCollaborator').and.returnValue();
		const spyRegisterUsers = spyOn(staffService, 'registerUser$').and.returnValue(of(new Collaborator()));

		component.connectionGroup.get('username').setValue('myPersonalUser');
		component.connectionGroup.get('password').setValue('myPersonalPass');
		component.connectionGroup.get('passwordConfirmation').setValue('myPersonalPass');
		fixture.detectChanges();

		const btnOk = fixture.debugElement.nativeElement.querySelector('#okButton');
		expect(btnOk).toBeDefined();
		btnOk.click();
		fixture.detectChanges();

		expect(spyRegisterUsers).toHaveBeenCalled();
		expect(spyChangeCollaborator).toHaveBeenCalled();
	});
});
