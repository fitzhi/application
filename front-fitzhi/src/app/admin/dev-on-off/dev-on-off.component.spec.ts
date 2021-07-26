import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { DevOnOffComponent } from './dev-on-off.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('DevOnOffComponent', () => {
	let component: DevOnOffComponent;
	let fixture: ComponentFixture<DevOnOffComponent>;

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ DevOnOffComponent ],
			imports: [RouterTestingModule.withRoutes([])]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(DevOnOffComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
