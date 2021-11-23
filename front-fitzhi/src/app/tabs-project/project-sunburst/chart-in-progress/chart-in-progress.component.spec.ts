import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ActivityLog } from 'src/app/data/activity-log';
import { Project } from 'src/app/data/project';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { FileService } from 'src/app/service/file.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { SunburstCinematicService } from '../service/sunburst-cinematic.service';
import { SsewatcherService } from '../ssewatcher/service/ssewatcher.service';
import { SSEWatcherComponent } from '../ssewatcher/ssewatcher.component';
import { ChartInProgressComponent } from './chart-in-progress.component';


describe('ChartInProgressComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	let sunburstCinematicService: SunburstCinematicService;
	let projectService: ProjectService;
	let ssewatcherService: SsewatcherService;

	@Component({
		selector: 'app-host-component',
		template:
			`
			<div style="width:600px;height:100px;background-color:whiteSmoke;margin-left:50px;margin-top:50px;">
				<app-chart-in-progress></app-chart-in-progress>
			</div>
			`
	})
	class TestHostComponent {
	}

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ ChartInProgressComponent, TestHostComponent, SSEWatcherComponent ],
			providers: [ReferentialService, ProjectService, CinematicService, MessageBoxService, FileService,
				SunburstCinematicService, SsewatcherService],
			imports: [MatProgressBarModule, HttpClientTestingModule, MatDialogModule, MatCardModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;

		sunburstCinematicService = TestBed.inject(SunburstCinematicService);

		projectService = TestBed.inject(ProjectService);
		projectService.project = new Project(1789, 'The great revolution');

		ssewatcherService = TestBed.inject(SsewatcherService);

		fixture.detectChanges();
	});

	it('Should create correctly the chart in progress component', waitForAsync(() => {
		expect(component).toBeTruthy();

		const eventSource = <EventSource>{};

		const spy = spyOn(ssewatcherService, 'listenServer').and.returnValue(eventSource);

		sunburstCinematicService.listenEventsFromServer$.next(true);
		ssewatcherService.event$.next(new ActivityLog({message: 'Application message', progressionPercentage: 50}));
		fixture.detectChanges();

	}));
});
