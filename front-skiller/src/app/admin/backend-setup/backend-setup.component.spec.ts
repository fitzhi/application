import { NO_ERRORS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BackendSetupComponent } from './backend-setup.component';
import { HttpClientModule } from '@angular/common/http';

describe('BackendSetupComponent', () => {
	let component: BackendSetupComponent;
	let fixture: ComponentFixture<BackendSetupComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ BackendSetupComponent ],
			imports: [HttpClientModule],
			schemas: [ NO_ERRORS_SCHEMA ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(BackendSetupComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
