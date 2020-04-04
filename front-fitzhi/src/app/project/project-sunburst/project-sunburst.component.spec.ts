import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';
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

describe('ProjectSunburstComponent', () => {
	let component: ProjectSunburstComponent;
	let fixture: ComponentFixture<ProjectSunburstComponent>;

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [ProjectSunburstComponent, NodeDetailComponent,
				ProjectGhostsComponent, TableDependenciesComponent, DialogLegendSunburstComponent,
				ListFilenamesComponent, ListContributorsComponent, TableGhostsComponent, SSEWatcherComponent],
			providers: [],
			imports: [MatSidenavModule, MatCardModule, RouterTestingModule.withRoutes([])]
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
		expect(component).toBeTruthy();
	});
});
