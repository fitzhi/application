import { ComponentFixture, TestBed, TestModuleMetadata, waitForAsync } from '@angular/core/testing';
import { ProjectSunburstComponent } from './project-sunburst.component';
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
import { MessageBoxComponent } from 'src/app/interaction/message-box/dialog/message-box.component';
import { SunburstCinematicService } from './service/sunburst-cinematic.service';
import { NgxPopper } from 'angular-popper';

describe('ProjectSunburstComponent', () => {
	let component: ProjectSunburstComponent;
	let fixture: ComponentFixture<ProjectSunburstComponent>;

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
		fixture.detectChanges();
	});

	it('should create', () => {
		setTimeout(() => {
			const sunburstCinematicService = TestBed.inject(SunburstCinematicService);
			sunburstCinematicService.activatedButton = component.UNKNOWN;
			fixture.detectChanges();
		}, 100);
		setTimeout(() => {
			const sunburstCinematicService = TestBed.inject(SunburstCinematicService);
			sunburstCinematicService.activatedButton = component.DEPENDENCIES;
			fixture.detectChanges();
		}, 400);
		setTimeout(() => {
			const sunburstCinematicService = TestBed.inject(SunburstCinematicService);
			sunburstCinematicService.activatedButton = component.RESET;
			fixture.detectChanges();
		}, 200);
		setTimeout(() => {
			const sunburstCinematicService = TestBed.inject(SunburstCinematicService);
			sunburstCinematicService.activatedButton = component.SETTINGS;
			fixture.detectChanges();
		}, 250);
		setTimeout(() => {
			const sunburstCinematicService = TestBed.inject(SunburstCinematicService);
			sunburstCinematicService.activatedButton = component.LEGEND_SUNBURST;
			fixture.detectChanges();
		}, 300);
		setTimeout(() => {
			const sunburstCinematicService = TestBed.inject(SunburstCinematicService);
			sunburstCinematicService.activatedButton = component.SUNBURST;
			fixture.detectChanges();
		}, 350);
		expect(component).toBeTruthy();
	});
});
