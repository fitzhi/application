import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TechxhiMedalComponent } from './techxhi-medal.component';
import { ProjectService } from 'src/app/service/project.service';
import { Project } from 'src/app/data/project';
import { BehaviorSubject } from 'rxjs';
import { ReferentialService } from 'src/app/service/referential.service';
import { PortalHostDirective } from '@angular/cdk/portal';
import { SonarProject } from 'src/app/data/SonarProject';
import { SonarEvaluation } from 'src/app/data/sonar-evaluation';
import { RiskLegend } from 'src/app/data/riskLegend';
import { MatGridList, MatGridListModule } from '@angular/material/grid-list';
import { QuotationBadgeComponent } from '../../project-sonar/sonar-dashboard/sonar-quotation/quotation-badge/quotation-badge.component';
// tslint:disable-next-line:max-line-length
import { AuditGraphicBadgeComponent } from '../../project-audit/project-audit-badges/audit-badge/audit-graphic-badge/audit-graphic-badge.component';
import { FormsModule } from '@angular/forms';
import { CinematicService } from 'src/app/service/cinematic.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { Component, ViewChild } from '@angular/core';

describe('TechxhiMedalComponent', () => {
	let referentialService: ReferentialService;
	let projectService: ProjectService;
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template:
				`<div style="width: 400px; height: 400px; background-color: lightGrey">
					<app-techxhi-medal [colorOfRisk]="colorOfRisk" (tabActivationEmitter)="tabActivation($event)">
					</app-techxhi-medal>
				</div>`
	})
	class TestHostComponent {
		public colorOfRisk = 'green';
		/**
		 * This method receives the new tab to activate from e.g. the sunburst tab pane child
		 * (but it won't be the only one).
		 * @param tabIndex new tab to activate.
		 */
		public tabActivation (tabIndex: number) {
			console.log ('Selected index', tabIndex);
		}

	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [TechxhiMedalComponent, QuotationBadgeComponent, AuditGraphicBadgeComponent, TestHostComponent],
			providers: [ReferentialService, CinematicService],
			imports: [FormsModule, HttpClientTestingModule, MatDialogModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		referentialService = TestBed.inject(ReferentialService);
		referentialService.referentialLoaded$.next(true);
		projectService = TestBed.inject(ProjectService);

		referentialService.legendsLoaded$ = new BehaviorSubject<boolean>(false);
		// We create a mock of legends, which is not always used in the unit test.
		let i: number;
		for (i = 0; i <= 10; i++) {
			const rl  = new RiskLegend();
			rl.color = 'green';
			rl.level = i;
			rl.description = 'risk ' + i;
			referentialService.legends.push(rl);
		}

		component = fixture.componentInstance;
		const project = new Project();
		project.id = 1789;
		project.name = 'Revolutionary project';
		project.audit = {};

		project.sonarProjects = [];
		const sonarProjectOne =  new SonarProject();
		sonarProjectOne.key = '1';
		sonarProjectOne.name = 'One';
		sonarProjectOne.sonarEvaluation = new SonarEvaluation(50, 3000);
		project.sonarProjects.push(sonarProjectOne);

		project.auditEvaluation = 20;

		project.urlCodeFactorIO = 'https://www.codefactor.io/repository/github/fitzhi/application/badge/master?style=plastic';

		projectService = TestBed.inject(ProjectService);
		projectService.project = project;

		projectService.projectLoaded$ = new BehaviorSubject(true);

		fixture.detectChanges();
	});

	it('Displaying the Fitzhì medal with sample data.', () => {
		expect(component).toBeTruthy();
	});

});


