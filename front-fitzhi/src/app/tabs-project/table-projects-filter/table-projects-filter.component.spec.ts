import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, OnInit, ViewChild } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Project } from 'src/app/data/project';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { TableProjectsFilterComponent } from './table-projects-filter.component';


describe('TableProjectsFilterComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let projectService: ProjectService;

	@Component({
		selector: 'app-host-component',
		template: `<div style="width: 250px; height: 400px">
	  					<app-table-projects-filter></app-table-projects-filter>
					</div>`})
	class TestHostComponent implements OnInit {

		projects = [];

		@ViewChild(TableProjectsFilterComponent) tableProjectsFilterComponent: TableProjectsFilterComponent;

		constructor() { }

		ngOnInit(): void {
		}
	}

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [TableProjectsFilterComponent, TestHostComponent],
			providers: [ProjectService, ReferentialService, CinematicService],
			imports: [MatTableModule, MatCheckboxModule, BrowserAnimationsModule, FormsModule,
				HttpClientTestingModule, MatDialogModule]
		})
			.compileComponents();
		projectService = TestBed.inject(ProjectService);
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		projectService.allProjects = [];
		for (let i = 0; i < 10; i++) {
			projectService.allProjects.push(new Project(i, 'Project ' + i));
		}
		projectService.allProjectsIsLoaded$.next(true);
	});

	it('should be created without error.', done => {
		expect(component).toBeTruthy();
		fixture.detectChanges();
		done();
	});

	it('should de-select correctly a project if the user clicks on the corresponding name.', fakeAsync(() => {

		fixture.detectChanges();

		let prj = component.tableProjectsFilterComponent.dataSource.data.find(p => p.id === 3);
		expect(prj.selected).toBeTrue();

		const name = fixture.debugElement.query(By.css('#name-3'));
		name.nativeElement.click(); // triggerEventHandler('click', null);
		tick(); // simulates the passage of time until all pending asynchronous activities finish
		fixture.detectChanges();

		prj = component.tableProjectsFilterComponent.dataSource.data.find(p => p.id === 3);
		expect(prj.selected).toBeFalse();
	}));

	it('should de-select also the criteria ALL_PROJECTS if the user deselects a project.', fakeAsync(() => {

		fixture.detectChanges();

		let prj = component.tableProjectsFilterComponent.dataSource.data.find(p => p.id === -1);
		expect(prj.selected).toBeTrue();

		const name = fixture.debugElement.query(By.css('#name-3'));
		name.nativeElement.click(); // triggerEventHandler('click', null);
		tick(); // simulates the passage of time until all pending asynchronous activities finish
		fixture.detectChanges();

		prj = component.tableProjectsFilterComponent.dataSource.data.find(p => p.id === -1);
		expect(prj.selected).toBeFalse();

	}));

	it('should select correctly a project if the user clicks on the corresponding name.', fakeAsync(() => {

		let prj = component.tableProjectsFilterComponent.dataSource.data.find(p => p.id === 3);
		prj.selected = false;
		fixture.detectChanges();

		const name = fixture.debugElement.query(By.css('#name-3'));
		name.nativeElement.click(); // triggerEventHandler('click', null);
		tick(); // simulates the passage of time until all pending asynchronous activities finish
		fixture.detectChanges();

		prj = component.tableProjectsFilterComponent.dataSource.data.find(p => p.id === 3);
		expect(prj.selected).toBeTrue();
	}));

	it('should de-select ALL projects if the user clicks on the "ALL projects" item.', fakeAsync(() => {
		fixture.detectChanges();
		const name = fixture.debugElement.query(By.css('#name--1'));
		console.log(name);
		name.nativeElement.click(); // triggerEventHandler('click', null);
		tick(); // simulates the passage of time until all pending asynchronous activities finish
		fixture.detectChanges();
		component.tableProjectsFilterComponent.dataSource.data.forEach(
			p => {
				expect(p.selected).toBeFalse();
			}
		);
	}));
});

