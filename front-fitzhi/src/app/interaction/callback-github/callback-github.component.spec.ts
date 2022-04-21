import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CallbackGithubComponent } from './callback-github.component';


describe('CallbackGithubComponent', () => {
	let component: CallbackGithubComponent;
	let fixture: ComponentFixture<CallbackGithubComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ CallbackGithubComponent ]
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(CallbackGithubComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should be successfully created.', () => {
		expect(component).toBeTruthy();
	});
});
