import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterUserComponent } from './register-user.component';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpClient } from '@angular/common/http';
import { RootTestModule } from 'src/app/root-test/root-test.module';


describe('RegisterUserComponent', () => {
	let component: RegisterUserComponent;
	let fixture: ComponentFixture<RegisterUserComponent>;

	let httpClient: HttpClient;
	let httpTestingController: HttpTestingController;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [],
			providers: [],
			imports: [
				HttpClientTestingModule,
				RootTestModule,
			]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		// Inject the http service and test controller for each test
		httpClient = TestBed.get(HttpClient);
		httpTestingController = TestBed.get(HttpTestingController);

		fixture = TestBed.createComponent(RegisterUserComponent);
		component = fixture.componentInstance;

		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
