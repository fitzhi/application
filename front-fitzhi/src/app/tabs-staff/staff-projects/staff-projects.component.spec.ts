import { ComponentFixture, TestBed, TestModuleMetadata, waitForAsync } from '@angular/core/testing';

import { StaffProjectsComponent } from './staff-projects.component';
import { InitTest } from 'src/app/test/init-test';
import { Collaborator } from 'src/app/data/collaborator';
import { StaffService } from '../service/staff.service';

describe('StaffProjectsComponent', () => {
	let component: StaffProjectsComponent;
	let fixture: ComponentFixture<StaffProjectsComponent>;

	beforeEach(waitForAsync(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [StaffProjectsComponent, StaffProjectsComponent],
			providers: [],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
}));

	beforeEach(() => {
		fixture = TestBed.createComponent(StaffProjectsComponent);
		component = fixture.componentInstance;

		const staffService = TestBed.inject(StaffService);
		staffService.collaborator = new Collaborator();
		staffService.collaborator.idStaff = 10;
		staffService.collaborator.firstName = 'Kylian';
		staffService.collaborator.lastName = 'Mbappe';
		staffService.collaborator.missions = [];

		staffService.collaboratorLoaded$.next(true);

		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
