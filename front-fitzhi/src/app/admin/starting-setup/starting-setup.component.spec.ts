import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StartingSetupComponent } from './starting-setup.component';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { RouterTestingModule } from '@angular/router/testing';
import { RootTestModule } from 'src/app/root-test/root-test.module';


describe('StartingSetupComponent', () => {
	let component: StartingSetupComponent;
	let fixture: ComponentFixture<StartingSetupComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [  ],
			schemas: [ NO_ERRORS_SCHEMA ],
			imports: [
				RootTestModule,
				RouterTestingModule.withRoutes([]),
				HttpClientTestingModule
			]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(StartingSetupComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
