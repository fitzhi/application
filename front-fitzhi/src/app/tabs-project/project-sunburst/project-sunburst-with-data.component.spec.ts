
import { ComponentFixture, TestBed, TestModuleMetadata, waitForAsync } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatSidenavModule } from '@angular/material/sidenav';
import { RouterTestingModule } from '@angular/router/testing';
import { NgxPopper } from 'angular-popper';
import { Project } from 'src/app/data/project';
import { MessageBoxComponent } from 'src/app/interaction/message-box/dialog/message-box.component';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { InitTest } from 'src/app/test/init-test';
import { data } from './data-sunburst';
import { DialogLegendSunburstComponent } from './legend-sunburst/legend-sunburst.component';
import { ListContributorsComponent } from './node-detail/list-contributors/list-contributors.component';
import { ListFilenamesComponent } from './node-detail/list-filenames/list-filenames.component';
import { NodeDetailComponent } from './node-detail/node-detail.component';
import { ProjectGhostsComponent } from './project-ghosts/project-ghosts.component';
import { TableGhostsComponent } from './project-ghosts/table-ghosts/table-ghosts.component';
import { PreviewContext, ProjectSunburstComponent } from './project-sunburst.component';
import { SunburstCacheService } from './service/sunburst-cache.service';
import { SunburstCinematicService } from './service/sunburst-cinematic.service';
import { SSEWatcherComponent } from './ssewatcher/ssewatcher.component';
import { TableDependenciesComponent } from './table-dependencies/table-dependencies.component';

describe('ProjectSunburstComponent filled with data', () => {
	let component: ProjectSunburstComponent;
	let fixture: ComponentFixture<ProjectSunburstComponent>;
	let projectService: ProjectService;
	let backendSetupService: BackendSetupService;
	let cinematicService: CinematicService;
	let sunburstCinematicService: SunburstCinematicService;

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
		sunburstCinematicService = TestBed.inject(SunburstCinematicService);

	});

	it('should draw correctly the chart.', () => {

		component.activeContext = PreviewContext.SUNBURST_READY;
		fixture.detectChanges();

		setTimeout(() => {
			projectService.projectLoaded$.next(true);

			const sunburstCacheService = TestBed.inject(SunburstCacheService);
			sunburstCacheService.saveResponse(data);

			sunburstCinematicService.refreshChart$.next(true);
		}, 0);

		expect(component).toBeTruthy();

	});

	it('should hide the former chart (if present).', done => {

		component.activeContext = PreviewContext.SUNBURST_READY;
		fixture.detectChanges();


		setTimeout(() => {
			projectService.projectLoaded$.next(true);

			const sunburstCacheService = TestBed.inject(SunburstCacheService);
			sunburstCacheService.saveResponse(data);

			sunburstCinematicService.refreshChart$.next(true);
			
			setTimeout(() => {
				sunburstCinematicService.refreshChart$.subscribe({
					next: doneAndOk => {
						fixture.detectChanges();
						expect(component).toBeTruthy();
						component.hidePreviousSunburstChartDetector();
						const sunburstViz = fixture.debugElement.nativeElement.querySelector('.sunburst-viz');
						expect(sunburstViz).toBeTruthy();
						expect(sunburstViz.style.display).toBe('none');
						done();
					}
				});
			}, 200);
		}, 0);
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
