import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { StaffTabsComponent } from './staff-tabs.component';
import { TableGhostsComponent } from 'src/app/project/project-sunburst/project-ghosts/table-ghosts/table-ghosts.component';
import { InitTest } from 'src/app/test/init-test';
import { StaffProjectsComponent } from '../staff-projects/staff-projects.component';
import { StaffExperienceComponent } from '../staff-experience/staff-experience.component';
import { TagifyStarsComponent } from '../staff-experience/tagify-stars/tagify-stars.component';

describe('StaffTabsComponent', () => {
	let component: StaffTabsComponent;
	let fixture: ComponentFixture<StaffTabsComponent>;

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [StaffTabsComponent, StaffProjectsComponent, StaffExperienceComponent, TagifyStarsComponent],
			providers: [],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(StaffTabsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
