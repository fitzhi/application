import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { StaffProjectsComponent } from './staff-projects.component';
import { InitTest } from 'src/app/test/init-test';
import { StaffDataExchangeService } from '../service/staff-data-exchange.service';
import { Collaborator } from 'src/app/data/collaborator';

describe('StaffProjectsComponent', () => {
	let component: StaffProjectsComponent;
	let fixture: ComponentFixture<StaffProjectsComponent>;

	beforeEach(async(() => {
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

		const staffDataExchangeService = TestBed.get(StaffDataExchangeService);
		staffDataExchangeService.collaborator = new Collaborator();
		staffDataExchangeService.collaborator.idStaff = 10;
		staffDataExchangeService.collaborator.firstName = 'Kylian';
		staffDataExchangeService.collaborator.lastName = 'Mbappe';
		staffDataExchangeService.collaborator.missions = [];

		staffDataExchangeService.collaboratorLoaded$.next(true);

		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
