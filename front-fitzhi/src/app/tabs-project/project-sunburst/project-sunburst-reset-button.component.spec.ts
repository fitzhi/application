
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
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { BehaviorSubject, of } from 'rxjs';

describe('Testing the Reet button behavior in ProjectSunburstComponent with data', () => {
	let component: ProjectSunburstComponent;
	let fixture: ComponentFixture<ProjectSunburstComponent>;
	let messageBoxService: MessageBoxService;
	let projectService: ProjectService;
	let cacheService: SunburstCacheService;

	beforeEach(waitForAsync(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [ProjectSunburstComponent, NodeDetailComponent,
				ProjectGhostsComponent, TableDependenciesComponent, DialogLegendSunburstComponent,
				ListFilenamesComponent, ListContributorsComponent, TableGhostsComponent, SSEWatcherComponent, MessageBoxComponent],
			providers: [ProjectService, SunburstCacheService],
			imports: [MatSidenavModule, MatCardModule, NgxPopper, RouterTestingModule.withRoutes([])]
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(ProjectSunburstComponent);
		component = fixture.componentInstance;

		component.activeContext = PreviewContext.SUNBURST_READY;
		fixture.detectChanges();

		projectService = TestBed.inject(ProjectService);
		projectService.project = new Project(1789, 'Revolutionnary project');
		projectService.project.active = true;
		projectService.projectLoaded$.next(true);
		fixture.detectChanges();

		const sunburstCacheService = TestBed.inject(SunburstCacheService);
		sunburstCacheService.saveResponse(data);

		const sunburstCinematicService = TestBed.inject(SunburstCinematicService);
		sunburstCinematicService.refreshChart$.next(true);
		fixture.detectChanges();

		messageBoxService = TestBed.inject(MessageBoxService);
		cacheService = TestBed.inject(SunburstCacheService);
	});

	it('Click on reset : The end-user accepts the Re-generation', async () => {

		expect(component).toBeTruthy();

		const spyQuestion = spyOn(messageBoxService, 'question').and.returnValue(new BehaviorSubject(true));
		const spyResetDashboard = spyOn(projectService, 'resetDashboard').and.returnValue(of(''));
		const spyClearResponse = spyOn(cacheService, 'clearReponse').and.returnValue();
		const spyReloadDashboard = spyOn(projectService, 'reloadSunburst$');

		const button = fixture.debugElement.nativeElement.querySelector('#reset');
		expect(button).toBeDefined();
		button.click();
		fixture.detectChanges();

		expect(spyReloadDashboard).not.toHaveBeenCalled();

	});

	it('Click reset : The end-user refuses the Re-generation', async () => {

		expect(component).toBeTruthy();

		const spyQuestion = spyOn(messageBoxService, 'question').and.returnValue(new BehaviorSubject(false));
		const spyResetDashboard = spyOn(projectService, 'resetDashboard');
		const spyClearResponse = spyOn(cacheService, 'clearReponse');
		const spyReloadDashboard = spyOn(projectService, 'reloadSunburst$');


		const button = fixture.debugElement.nativeElement.querySelector('#reset');
		expect(button).toBeDefined();
		button.click();
		fixture.detectChanges();

		expect(spyResetDashboard).not.toHaveBeenCalled();
		expect(spyClearResponse).not.toHaveBeenCalled();
		expect(spyReloadDashboard).not.toHaveBeenCalled();

	});



});
