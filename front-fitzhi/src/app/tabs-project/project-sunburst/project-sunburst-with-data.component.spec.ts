
import { ComponentFixture, TestBed, TestModuleMetadata, waitForAsync } from '@angular/core/testing';
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
import { HttpTestingController } from '@angular/common/http/testing';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { ProjectContributors } from 'src/app/data/external/ProjectContributors';
import { ExpectedConditions } from 'protractor';
import { CinematicService } from 'src/app/service/cinematic.service';
import { TestabilityRegistry } from '@angular/core';
import { Constants } from 'src/app/constants';

describe('ProjectSunburstComponent with data', () => {
	let component: ProjectSunburstComponent;
	let fixture: ComponentFixture<ProjectSunburstComponent>;
	let projectService: ProjectService;
	let backendSetupService: BackendSetupService;
	let cinematicService: CinematicService;

	beforeEach(waitForAsync(() => {
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

		projectService = TestBed.inject(ProjectService);
		projectService.project = new Project(1789, 'Revolutionnary project');
		projectService.project.active = true;

		backendSetupService = TestBed.inject(BackendSetupService);
		backendSetupService.saveUrl('HOST_URL');


		cinematicService = TestBed.inject(CinematicService);

	});

	it('drawing the chart', () => {

		component.activeContext = PreviewContext.SUNBURST_READY;
		fixture.detectChanges();

		setTimeout(() => {
			projectService.projectLoaded$.next(true);

			const sunburstCacheService = TestBed.inject(SunburstCacheService);
			sunburstCacheService.saveResponse(data);

			const sunburstCinematicService = TestBed.inject(SunburstCinematicService);
			sunburstCinematicService.refreshChart$.next(true);
		}, 0);

		expect(component).toBeTruthy();

	});
/*
	it('handling a conflict error from the HttpClient', done => {
		component.activeContext = PreviewContext.SUNBURST_READY;
		fixture.detectChanges();

			projectService.projectLoaded$.next(true);

			const sunburstCacheService = TestBed.inject(SunburstCacheService);
			sunburstCacheService.clearReponse();

			const sunburstCinematicService = TestBed.inject(SunburstCinematicService);
			sunburstCinematicService.refreshChart$.next(true);

			const httpMock = TestBed.inject(HttpTestingController);

			const reqSkills = httpMock.expectOne(`HOST_URL/api/skill`);
			reqSkills.flush([]);

			setTimeout(() => {

				cinematicService.tabProjectActivatedSubject$.next(Constants.PROJECT_IDX_TAB_SUNBURST);

				const reqContributors = httpMock.expectOne(`HOST_URL/api/project/1789/contributors`);
				reqContributors.flush( { "idProject": 1789, "contributors": [] });

				const reqSunburst = httpMock.expectOne(`HOST_URL/api/project/1789/sunburst`);
				expect(reqSunburst.request.method).toBe('PUT');
				reqSunburst.flush({
					status: 409,
					statusText: 'Error code'
				});
				httpMock.verify();

				done();
			}, 0);

		expect(component).toBeTruthy();

	});
*/

});
