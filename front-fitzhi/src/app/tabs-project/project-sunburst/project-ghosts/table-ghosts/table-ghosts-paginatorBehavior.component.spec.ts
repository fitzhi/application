import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { TableGhostsComponent } from './table-ghosts.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { FormsModule } from '@angular/forms';
import { InitTest } from 'src/app/test/init-test';
import { Component, ViewChild } from '@angular/core';
import { BehaviorSubject, of } from 'rxjs';
import { Unknown } from '../../../../data/unknown';
import { Collaborator } from 'src/app/data/collaborator';
import { ProjectGhostsDataSource } from '../project-ghosts-data-source';
import { Project } from 'src/app/data/project';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
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

		@ViewChild(TableGhostsComponent) tableGhostsComponent: TableGhostsComponent;

		public ghosts: Unknown[];

		constructor(projectService: ProjectService) {

			projectService.project = new Project(1789, 'Revolutionary project');

			this.ghosts = 				[
				{
					idStaff: -1,
					pseudo: 'one',
					login: '',
					firstname: 'One',
					lastname: 'One',
					fullName: '',
					technical: false,
					active: false,
					external: false,
					action: '',
					staffRelated: new Collaborator(),
					staffRecorded: false,
				},
				{
					idStaff: -1,
					pseudo: 'two',
					login: '',
					firstname: 'two',
					lastname: 'Two',
					fullName: '',
					technical: false,
					active: false,
					external: false,
					action: '',
					staffRelated: new Collaborator(),
					staffRecorded: false,
				},
				{
					idStaff: -1,
					pseudo: 'three',
					login: '',
					firstname: 'three',
					lastname: 'Three',
					fullName: '',
					technical: false,
					active: false,
					external: false,
					action: '',
					staffRelated: new Collaborator(),
					staffRecorded: false,
				},
				{
					idStaff: -1,
					pseudo: 'four',
					login: '',
					firstname: 'four',
					lastname: 'Four',
					fullName: '',
					technical: false,
					active: false,
					external: false,
					action: '',
					staffRelated: new Collaborator(),
					staffRecorded: false,
				},
				{
					idStaff: -1,
					pseudo: 'five',
					login: '',
					firstname: 'five',
					lastname: 'Five',
					fullName: '',
					technical: false,
					active: false,
					external: false,
					action: '',
					staffRelated: new Collaborator(),
					staffRecorded: false,
				},
				{
					idStaff: -1,
					pseudo: 'six',
					login: '',
					firstname: 'six',
					lastname: 'Six',
					fullName: '',
					technical: false,
					active: false,
					external: false,
					action: '',
					staffRelated: new Collaborator(),
					staffRecorded: false,
				},
				{
					idStaff: -1,
					pseudo: 'seven',
					login: '',
					firstname: 'seven',
					lastname: 'Sevent',
					fullName: '',
					technical: false,
					active: false,
					external: false,
					action: '',
					staffRelated: new Collaborator(),
					staffRecorded: false,
				},
				{
					idStaff: -1,
					pseudo: 'height',
					login: '',
					firstname: 'heigth',
					lastname: 'Height',
					fullName: '',
					technical: false,
					active: false,
					external: false,
					action: '',
					staffRelated: new Collaborator(),
					staffRecorded: false,
				},
			];

			this.projectGhostsDataSource = new ProjectGhostsDataSource(this.ghosts, []);
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

	// 5 lines maximum in the ghosts list.
	it('should handle correctly the paginator Behavior', () => {
		expect(component).toBeTruthy();
		expect(component.projectGhostsDataSource.data.length).toBe(8);
		fixture.detectChanges();

		const buttonLine3 = fixture.debugElement.nativeElement.querySelector('#addStaff-3');
		expect(buttonLine3).toBeDefined();

		const buttonAbsent = fixture.debugElement.nativeElement.querySelector('#addStaff-6');
		expect(buttonAbsent).toBeNull();


		const staffService = TestBed.inject(StaffService);
		const spy = spyOn(staffService, 'save$')
			.and.returnValue(of(
				{
					idStaff: 1964,
					pseudo: 'frvidal',
					login: 'frvidal',
					firstName: 'Frédéric',
					lastName: 'VIDAL',
					nickName: 'frvidal',
					fullName: 'Frédéric VIDAL',
					email: 'frederic.vidal@fitzhi.com',
					level: 'Developper',
					forceActiveState: false,
					external: false,
					active:  true,
					dateInactive: null,
					experiences: [],
					missions: []
				}));

		buttonLine3.click();
		fixture.detectChanges();
		expect(component.projectGhostsDataSource.ghosts.length).toBe(7);

		const line5 = fixture.debugElement.nativeElement.querySelector('#firstname-4');
		expect(line5).toBeDefined();
		const attributes: any[] = line5.attributes;
		for (let i = 0; i < attributes.length; i++) {
			const o = attributes[i];
			if  (o.name === 'ng-reflect-model') {
				console.log (o.value);
				expect(o.value).toBe('six');
			}
		}

	});


});

