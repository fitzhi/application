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

describe('PieProjectsComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

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
			declarations: [ TestHostComponent, PieProjectsComponent ],
			imports: [MatTableModule, HttpClientTestingModule, RouterTestingModule, MatDialogModule, MatPaginatorModule, BrowserAnimationsModule],
			providers: [ReferentialService, CinematicService, PieDashboardService]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create the projects list linked to selected slice', () => {
		expect(component).toBeTruthy();
	});
});
