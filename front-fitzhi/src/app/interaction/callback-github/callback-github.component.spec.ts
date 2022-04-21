import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { GithubToken } from 'src/app/data/github-token';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { CallbackGithubComponent } from './callback-github.component';


describe('CallbackGithubComponent', () => {
	let component: CallbackGithubComponent;
	let fixture: ComponentFixture<CallbackGithubComponent>;
	let httpTestingController: HttpTestingController;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ CallbackGithubComponent ],
			providers: [
				{
					provide: ActivatedRoute,
					useValue: {
						snapshot: {queryParams: {code: 'thecode'}}
					}
				}
			],
			imports: [
				HttpClientTestingModule,
				RouterTestingModule.withRoutes([])
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(CallbackGithubComponent);
		component = fixture.componentInstance;
		httpTestingController = TestBed.inject(HttpTestingController);

		fixture.detectChanges();
	});

	it('should be successfully created.', () => {
		expect(component).toBeTruthy();
	});

	it('should invoke the Fitzhi backend with the code sent by Github.', () => {
		const backendSetupService = TestBed.inject(BackendSetupService);
		backendSetupService.saveUrl('URL_OF_SERVER');
		fixture.detectChanges();

		const req = httpTestingController.expectOne('URL_OF_SERVER/api/admin/openId/primeRegister');
		expect(req.request.method).toBe('POST');
		// expect(req.request.body).toBe('username=my-user&password=my-password&grant_type=password'); // This is not a credential. //NOSONAR
		const t = new GithubToken();
		t.access_token = '1234';
		t.scope = 'read, write';
		t.token_type = 'token'
		req.flush(t);

	});

});
