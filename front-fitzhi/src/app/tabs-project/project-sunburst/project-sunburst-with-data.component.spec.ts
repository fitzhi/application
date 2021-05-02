
import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';
import { ProjectSunburstComponent, PreviewContext } from './project-sunburst.component';
import { InitTest } from 'src/app/test/init-test';
import { MatSidenavModule } from '@angular/material/sidenav';
import { NodeDetailComponent } from './node-detail/node-detail.component';
import { MatCardModule } from '@angular/material/card';
import { ProjectGhostsComponent } from './project-ghosts/project-ghosts.component';
import { TableDependenciesComponent } from './table-dependencies/table-dependencies.component';
import { DialogLegendSunburstComponent } from './legend-sunburst/legend-sunburst.component';
import { ListFilenamesComponent } from './node-detail/list-filenames/list-filenames.component';
import { ListContributorsComponent } from './node-detail/list-contributors/list-contributors.component';
import { TableGhostsComponent } from './project-ghosts/table-ghosts/table-ghosts.component';
import { RouterTestingModule } from '@angular/router/testing';
import { SSEWatcherComponent } from './ssewatcher/ssewatcher.component';
import { ProjectService } from 'src/app/service/project/project.service';
import { Project } from 'src/app/data/project';
import { MessageBoxComponent } from 'src/app/interaction/message-box/dialog/message-box.component';
import { SunburstCinematicService } from './service/sunburst-cinematic.service';
import { NgxPopper } from 'angular-popper';
import { data } from './data-sunburst';
import { SunburstCacheService } from './service/sunburst-cache.service';

describe('ProjectSunburstComponent with data', () => {
	let component: ProjectSunburstComponent;
	let fixture: ComponentFixture<ProjectSunburstComponent>;

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [ProjectSunburstComponent, NodeDetailComponent,
				ProjectGhostsComponent, TableDependenciesComponent, DialogLegendSunburstComponent,
				ListFilenamesComponent, ListContributorsComponent, TableGhostsComponent, SSEWatcherComponent, MessageBoxComponent],
			providers: [],
			imports: [MatSidenavModule, MatCardModule, NgxPopper, RouterTestingModule.withRoutes([])]
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(ProjectSunburstComponent);
		component = fixture.componentInstance;

	});

	it('drawing the chart', () => {

		component.activeContext = PreviewContext.SUNBURST_READY;
		fixture.detectChanges();

		setTimeout(() => {
			const projectService = TestBed.inject(ProjectService);
			projectService.project = new Project(1789, 'Revolutionnary project');
			projectService.project.active = true;
			projectService.projectLoaded$.next(true);

			const sunburstCacheService = TestBed.inject(SunburstCacheService);
			sunburstCacheService.saveResponse(data);

			const sunburstCinematicService = TestBed.inject(SunburstCinematicService);
			sunburstCinematicService.refreshChart$.next(true);
		}, 0);

		expect(component).toBeTruthy();

	});
});
