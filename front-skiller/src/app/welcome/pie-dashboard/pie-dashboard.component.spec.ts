import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PieDashboardComponent } from './pie-dashboard.component';

describe('PieDashboardComponent', () => {
	let component: PieDashboardComponent;
	let fixture: ComponentFixture<PieDashboardComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ PieDashboardComponent ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(PieDashboardComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
