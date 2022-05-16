import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { InstallService } from 'src/app/admin/service/install/install.service';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { CallbackGithubComponent } from './callback-github.component';


describe('CallbackGithubComponent (when registering user)', () => {
	let component: CallbackGithubComponent;
	let fixture: ComponentFixture<CallbackGithubComponent>;
	let httpTestingController: HttpTestingController;
	let installService: InstallService;
	let spyPrimeRegister: any;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ CallbackGithubComponent ],
			providers: [
				{
					provide: ActivatedRoute,
					useValue: {
						snapshot: {queryParams: {code: 'thecode'}}
					}
				},
				CinematicService,
			],
			imports: [
				MatDialogModule,
				HttpClientTestingModule,
				RouterTestingModule.withRoutes([])
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(CallbackGithubComponent);
		component = fixture.componentInstance;
		httpTestingController = TestBed.inject(HttpTestingController);

		const backendSetupService = TestBed.inject(BackendSetupService);
		backendSetupService.saveUrl('URL_OF_SERVER');

		installService = TestBed.inject(InstallService);
		spyOn(installService, 'isComplete').and.returnValue(false);

		spyPrimeRegister = spyOn(component, 'register').and.returnValue(null);

		fixture.detectChanges();
	});

	it('should register the curent user, if installation on desktop has NOT been completed..', () => {
		expect(spyPrimeRegister).toHaveBeenCalled();
	});

});
