import { HttpClient, HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSliderModule } from '@angular/material/slider';
import { MatTableModule } from '@angular/material/table';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { InstallService } from '../service/install/install.service';
import { RegisterUserFormComponent } from './register-user-form/register-user-form.component';
import { RegisterUserComponent } from './register-user.component';


describe('RegisterUserComponent', () => {
	let component: RegisterUserComponent;
	let fixture: ComponentFixture<RegisterUserComponent>;

	let httpClient: HttpClient;
	let httpTestingController: HttpTestingController;
	let referentialService: ReferentialService;
	
	let installService: InstallService;

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
			declarations: [RegisterUserComponent, RegisterUserFormComponent],
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
		referentialService = TestBed.inject(ReferentialService);
		referentialService.authenticationServers$.next( [ ]);

		fixture.detectChanges();

		component.registerUserFormComponent.connectionGroup.get('username').setValue('myPersonalUser');
		component.registerUserFormComponent.connectionGroup.get('password').setValue('myPersonalPass');
		component.registerUserFormComponent.connectionGroup.get('passwordConfirmation').setValue('myPersonalPass');
		fixture.detectChanges();

	});

	it('should display the Google connection if the openid google authentication server has been declared.', () => {
		const localOauthOnly = fixture.debugElement.nativeElement.querySelector('#localOauthOnly');
		expect(localOauthOnly).not.toBeNull();
		const multiOauth = fixture.debugElement.nativeElement.querySelector('#multipleOauth');
		expect(multiOauth).toBeNull();
	});


});
