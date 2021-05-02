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
import { ProjectService } from 'src/app/service/project/project.service';
import { SunburstCacheService } from '../../service/sunburst-cache.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';

describe('TableGhostsComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let sunburstCacheService: SunburstCacheService;
	let projectService: ProjectService;
	let staffListService: StaffListService;

	let allStaff = [
		{
			idStaff: 1964,
			login: 'frvidal',
			firstName: 'Frédéric',
			lastName: 'VIDAL',
			nickName: 'frvidal',
			email: 'frederic.vidal@fitzhi.com',
			level: 'Developper',
			external: false,
			forceActiveState: false,
			active: true,
			dateInactive: null,
			experiences: [],
			missions: []
		}
	];

	@Component({
		selector: 'app-host-component',
		template:
			`
			<h2 style="margin: 10px">The Ghosts...</h2>
			
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

		constructor(projectService: ProjectService) {

			projectService.project = new Project(1789, 'Revolutionary project');

			this.projectGhostsDataSource = new ProjectGhostsDataSource(
				[
					{
						idStaff: -1,
						pseudo: 'chaddock',
						login: '',
						firstname: 'Captain',
						lastname: 'Haddock',
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
						pseudo: 'frvidal',
						login: '',
						firstname: 'f',
						lastname: 'l',
						fullName: '',
						technical: false,
						active: false,
						external: false,
						action: '',
						staffRelated: new Collaborator(),
						staffRecorded: false,
					},
				],
				allStaff
			);
			this.dataSourceGhosts$ = new BehaviorSubject(this.projectGhostsDataSource);
		}
	}

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [TestHostComponent, TableGhostsComponent],
			providers: [StaffService, ProjectService, SunburstCacheService, ProjectService, StaffListService],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		sunburstCacheService = TestBed.inject(SunburstCacheService);
		projectService = TestBed.inject(ProjectService);
		staffListService = TestBed.inject(StaffListService);
		fixture.detectChanges();
	});

	it('should be dynamic', () => {
		expect(component).toBeTruthy();
		component.projectGhostsDataSource.data.push(
			{
				idStaff: -1,
				pseudo: 'mac',
				login: '',
				firstname: 'Emmanuel',
				lastname: 'Macron',
				fullName: '',
				technical: false,
				active: false,
				external: false,
				action: '',
				staffRelated: new Collaborator(),
				staffRecorded: false,
			}
		);
		console.log ('Data length', component.projectGhostsDataSource.data.length);
		component.tableGhostsComponent.renderRows();
		fixture.detectChanges();

		const buttonNewLine = fixture.debugElement.nativeElement.querySelector('#addStaff-2');
		expect(buttonNewLine).toBeDefined();
	});

	it('Create a simple collaborator', () => {
		expect(component).toBeTruthy();

		const spyClearCache = spyOn(sunburstCacheService, 'clearReponse');
		
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
					external: false,
					forceActiveState: false,
					active: true,
					dateInactive: null,
					experiences: [],
					missions: []
				}));

		const button = fixture.debugElement.nativeElement.querySelector('#addStaff-1');
		expect(button).toBeDefined();
		button.click();
		fixture.detectChanges();

		const buttonDeleted = fixture.debugElement.nativeElement.querySelector('#addStaff-1');
		expect(buttonDeleted).toBeNull();

		expect(spyClearCache).toHaveBeenCalled();
	});

	it('Connect a ghost to ...in fact... nobody, because there is no existing developer for the given pseudo', () => {

		const spyClearCache = spyOn(sunburstCacheService, 'clearReponse');
		const spyUpdateGhost$ = spyOn(projectService, 'updateGhost$');

		let login: HTMLInputElement = fixture.debugElement.query(By.css('#login-1')).nativeElement;
		expect(login).toBeDefined();
		login.value = 'some Value';
		login.dispatchEvent(new Event('input'));

		expect(spyClearCache).not.toHaveBeenCalled();
		expect(spyUpdateGhost$).not.toHaveBeenCalled();

		component.tableGhostsComponent.renderRows();
		fixture.detectChanges();

		const buttonAddStaff = fixture.debugElement.nativeElement.querySelector('#addStaff-1');
		expect(buttonAddStaff).toBeDefined();


	});

	it('Connect a ghost the existing developer frvidal for the given login', () => {

		const buttonAddStaff: HTMLInputElement = fixture.debugElement.query(By.css('#addStaff-1')).nativeElement;
		expect(buttonAddStaff.disabled).toBeFalsy();

		let firstName = fixture.debugElement.query(By.css('#firstname-1')).nativeElement;
		expect(firstName.value).toBe('');

		const lastName = fixture.debugElement.query(By.css('#lastname-1')).nativeElement;
		expect(lastName.value).toBe('');

		const spyClearCache = spyOn(sunburstCacheService, 'clearReponse');
		const spyUpdateGhost$ = spyOn(projectService, 'updateGhost$').and.returnValue(of(true));

		staffListService.allStaff$.next(allStaff);

		let login: HTMLInputElement = fixture.debugElement.query(By.css('#login-1')).nativeElement;
		expect(login).toBeDefined();
		login.value = 'frvidal';
		login.dispatchEvent(new Event('input'));

		expect(spyClearCache).toHaveBeenCalled();
		expect(spyUpdateGhost$).toHaveBeenCalled();

		component.tableGhostsComponent.renderRows();
		fixture.detectChanges();

		expect(buttonAddStaff.disabled).toBeTrue();
		expect(fixture.debugElement.query(By.css('#firstname-1'))).toBeNull();
		expect(fixture.debugElement.query(By.css('#lastname-1'))).toBeNull();

	});

});

