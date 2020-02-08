import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { ProjectGhostsComponent } from './project-ghosts.component';
import { TableGhostsComponent } from './table-ghosts/table-ghosts.component';
import { MatTableModule } from '@angular/material/table';
import { InitTest } from 'src/app/test/init-test';

describe('ProjectGhostsComponent', () => {
	let component: ProjectGhostsComponent;
	let fixture: ComponentFixture<ProjectGhostsComponent>;

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [ ProjectGhostsComponent, TableGhostsComponent],
			providers: [],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(ProjectGhostsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
