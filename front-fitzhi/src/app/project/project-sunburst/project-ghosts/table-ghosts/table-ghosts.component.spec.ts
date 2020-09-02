import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { TableGhostsComponent } from './table-ghosts.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { FormsModule } from '@angular/forms';
import { InitTest } from 'src/app/test/init-test';
import { Component } from '@angular/core';
import { BehaviorSubject, of } from 'rxjs';
import { Unknown } from '../../../../data/unknown';
import { Collaborator } from 'src/app/data/collaborator';
import { ProjectGhostsDataSource } from '../project-ghosts-data-source';
import { Project } from 'src/app/data/project';
import { StaffService } from 'src/app/service/staff.service';
import { By } from '@angular/platform-browser';
import { ProjectService } from 'src/app/service/project.service';

describe('TableGhostsComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: 
			`
			<p>Ghosts list</p>
			<div style="width:80%;height:50%">
				<app-table-ghosts [dataSourceGhosts$]="dataSourceGhosts$">
				</app-table-ghosts>
			</div>
			`
	})
	class TestHostComponent {

		public dataSourceGhosts$: BehaviorSubject<ProjectGhostsDataSource>;

		public projectGhostsDataSource: ProjectGhostsDataSource;

		constructor() {
			
			const project = new Project(1789, 'Revolutionary project');

			this.projectGhostsDataSource = new ProjectGhostsDataSource(project, []);

			this.projectGhostsDataSource.update(
				[
					{
						idStaff: -1,
						pseudo: 'frvidal',
						login: '',
						firstname: '',
						lastname: '',
						fullName: '',
						technical:false,
						active: false,
						external: false,
						action: '',
						staffRelated: new Collaborator(),
						staffRecorded: false,				
					},
					{
						idStaff: -1,
						pseudo: 'chaddock',
						login: '',
						firstname: 'Captain',
						lastname: 'Haddock',
						fullName: '',
						technical:false,
						active: false,
						external: false,
						action: '',
						staffRelated: new Collaborator(),
						staffRecorded: false,				
					}
				]
			);
			this.dataSourceGhosts$ = new BehaviorSubject(this.projectGhostsDataSource);
		}
	}

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [TestHostComponent, TableGhostsComponent],
			providers: [StaffService, ProjectService],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('Create a simple collaborator', () => {
		expect(component).toBeTruthy();

		const staffService = TestBed.get(StaffService);
		const spy = spyOn(staffService, 'save$')
			.and.returnValue(of(
				{
					idStaff: 1964,
					pseudo: 'frvidal',
					login: 'frvidal',
					firstname: 'Frédéric',
					lastname: 'VIDAL',
					nickName: 'frvidal',
					fullName: 'Frédéric VIDAL',
					email: 'frederic.vidal@fitzhi.com',
					level: 'Developper'
				}));

		
	});
});

