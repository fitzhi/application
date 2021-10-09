import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, OnInit } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';
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
		projects = []

		constructor() { }

		ngOnInit(): void {
			console.log(this.projects);
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

	it('should create', done => {
		expect(component).toBeTruthy();
		fixture.detectChanges();
		done();
	});
});
