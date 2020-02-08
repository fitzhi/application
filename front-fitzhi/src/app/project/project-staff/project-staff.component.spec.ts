import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { ProjectStaffComponent } from './project-staff.component';
import { Component } from '@angular/core';
import { Project } from 'src/app/data/project';
import { BehaviorSubject } from 'rxjs';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReferentialService } from 'src/app/service/referential.service';
import { MatDialogModule } from '@angular/material/dialog';
import { RouterModule } from '@angular/router';
import { CinematicService } from 'src/app/service/cinematic.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { InitTest } from 'src/app/test/init-test';

describe('ProjectStaffComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: 	'<app-project-staff></app-project-staff>'
	})
	class TestHostComponent {
		public project$ = new BehaviorSubject<Project>(new Project());
	}

	beforeEach(async(() => {
		const testConf: TestModuleMetadata  =  {
			declarations: [TestHostComponent, ProjectStaffComponent ],
			providers: [],
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

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
