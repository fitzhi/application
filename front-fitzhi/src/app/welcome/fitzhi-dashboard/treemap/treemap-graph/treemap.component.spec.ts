import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TreemapComponent } from './treemap.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ProjectService } from 'src/app/service/project.service';
import { ReferentialService } from 'src/app/service/referential.service';

describe('TreemapComponent', () => {
	let component: TreemapComponent;
	let fixture: ComponentFixture<TreemapComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			imports: [NgxChartsModule, BrowserAnimationsModule, HttpClientTestingModule, MatDialogModule],
			declarations: [ TreemapComponent ],
			providers: [DashboardService, ProjectService, ReferentialService]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TreemapComponent);
		component = fixture.componentInstance;
		component.distribution =  [
			{
				name: 'java',
				value: '50'
			},
			{
				name: '.Net',
				value: '20'
			},
			{
				name: 'Typescript',
				value: '30'
			}
		];
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
