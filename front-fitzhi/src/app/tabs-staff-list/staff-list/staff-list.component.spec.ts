import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { StaffListComponent } from './staff-list.component';
import { InitTest } from 'src/app/test/init-test';
import { RouterModule } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
describe('StaffListComponent', () => {
	let component: StaffListComponent;
	let fixture: ComponentFixture<StaffListComponent>;

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [StaffListComponent],
			providers: [],
			imports: [RouterTestingModule]
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(StaffListComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should be created without error', () => {
		expect(component).toBeTruthy();
	});

});
