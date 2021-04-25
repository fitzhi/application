import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PieProjectsComponent } from './pie-projects.component';
import { MatTableModule } from '@angular/material/table';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReferentialService } from 'src/app/service/referential.service';
import { RouterTestingModule } from '@angular/router/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { CinematicService } from 'src/app/service/cinematic.service';
import { Component } from '@angular/core';
import { PieDashboardService } from '../service/pie-dashboard.service';
import { Slice } from 'dynamic-pie-chart';
import { Project } from 'src/app/data/project';
import { MatPaginatorModule } from '@angular/material/paginator';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { ProjectFormComponent } from 'src/app/tabs-project/project-form/project-form.component';

describe('PieProjectsComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let router: Router;

	@Component({
		selector: 'app-host-component',
		template: ` <div style="height: 700px; width: 200px; background-color: whiteSmoke">
						<app-pie-projects>
						</app-pie-projects>
					</div>`
	})
	class TestHostComponent {

		constructor(pieDashboardService: PieDashboardService) {
			const staffs = [];
			const children = [];
			for (let i = 0; i < 30; i++) {
				children.push (new Project(i, 'File ' + i));
			}
			pieDashboardService.sliceActivated$.next(
				new Slice(5, 0, 30, 0, 'lightGreen', 'textColor', '12', children, true, false, null));
		}
	}


	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ TestHostComponent, PieProjectsComponent, ProjectFormComponent ],
			imports: [MatTableModule, HttpClientTestingModule, 
				RouterTestingModule.withRoutes([		
					{ path: 'project/:id', component: ProjectFormComponent }
				]),
			MatDialogModule, MatPaginatorModule, BrowserAnimationsModule],
			providers: [ReferentialService, CinematicService, PieDashboardService]
		})
		.compileComponents();
	}));
	
	beforeEach(() => {
		localStorage.setItem('pie-projects-staff.pageSize', '5');
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		router = TestBed.inject(Router);
		fixture.detectChanges();
	});

	it('should create the 5 elements first-page list  of of Projects associated with the selected slice', () => {
		expect(component).toBeTruthy();
		for (let i = 0; i < 5; i++) {
			const project = fixture.debugElement.query(By.css('#project-' + i));
			expect(project).toBeDefined();
		}
		// The project 5 is not present
		const project = fixture.debugElement.query(By.css('#project-5'));
		expect(project).toBeNull();
	});

	it('should create the 10 elements first-page list of of Projects associated with the selected slice', () => {
		expect(component).toBeTruthy();

		//
		// We set in th local storage that the end-user has chosen to have a 10-lines table
		//
		localStorage.setItem('pie-projects-staff.pageSize', '10');
		
		for (let i = 0; i < 10; i++) {
			const project = fixture.debugElement.query(By.css('#project-' + i));
			expect(project).toBeDefined();
		}
		// The project 10 is not present
		const project = fixture.debugElement.query(By.css('#project-10'));
		expect(project).toBeNull();
	});

	it('should route the application to the Project form with the selected project in table', () => {
		const project = fixture.debugElement.query(By.css('#project-2'));
		expect(project).toBeDefined();
		project.nativeElement.click();

		const navigateSpy = spyOn(router, 'navigate');

		fixture.detectChanges();

		expect(navigateSpy).not.toHaveBeenCalledWith(['project/2']);

	});


	it('should highlight the activate project in the table', () => {
		const project = fixture.debugElement.query(By.css('#project-2'));
		expect(project).toBeDefined();
		project.nativeElement.dispatchEvent(
			new MouseEvent('mouseenter', {
				view: window,
				bubbles: true,
				cancelable: true
			})
		);	

		fixture.detectChanges();
		expect(project.nativeElement.style.backgroundColor).toBe('lightgreen');

		const projectInactive = fixture.debugElement.query(By.css('#project-1'));
		expect(projectInactive.nativeElement.style.backgroundColor).toBe('');

	});

	it('should inactive the left project in the table', () => {
		const project = fixture.debugElement.query(By.css('#project-2'));
		expect(project).toBeDefined();
		project.nativeElement.dispatchEvent(
			new MouseEvent('mouseenter', {
				view: window,
				bubbles: true,
				cancelable: true
			})
		);	
		fixture.detectChanges();
		expect(project.nativeElement.style.backgroundColor).toBe('lightgreen');

		project.nativeElement.dispatchEvent(
			new MouseEvent('mouseleave', {
				view: window,
				bubbles: true,
				cancelable: true
			})
		);	
		expect(project.nativeElement.style.backgroundColor).toBe('');

	});

});
