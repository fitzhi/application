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
				`<div style="background-color: red; width: 100px; height: 200px;">
					<app-techxhi-medal [colorOfRisk]="none">
					</app-techxhi-medal>
				</div>`
	})
	class TestHostComponent {
		@ViewChild(TechxhiMedalComponent) techxhiMedalComponent: TechxhiMedalComponent;
	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [TechxhiMedalComponent, QuotationBadgeComponent, AuditGraphicBadgeComponent],
			providers: [ReferentialService, CinematicService],
			imports: [FormsModule, HttpClientTestingModule, MatDialogModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		referentialService = TestBed.inject(ReferentialService);
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

		projectService.project = new Project();
		projectService.projectLoaded$ = new BehaviorSubject<boolean>(true);

		fixture.detectChanges();
	});

	it('Fitzhì should not display the Sonar summary badge without color legends loaded', () => {
		expect(component).toBeTruthy();

		function field(id: string): HTMLInputElement {
			return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
		}
		expect(field('#sonarSummaryBadge')).toBeNull();

		referentialService.legendsLoaded$.next(true);

		expect(field('#sonarSummaryBadge')).toBeDefined();

	});

	it('Fitzhì should not display the Sonnar summary badge without any associated Sonar project', () => {
		function field(id: string): HTMLInputElement {
			return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
		}

		referentialService.legendsLoaded$.next(true);
		const p1 = new Project();
		projectService.project = p1;
		projectService.projectLoaded$.next(true);
		fixture.detectChanges();
		expect(field('#sonarSummaryBadge')).toBeNull();

		p1.sonarProjects = [];
		expect(field('#sonarSummaryBadge')).toBeNull();

		p1.sonarProjects.push(new SonarProject());
		expect(field('#sonarSummaryBadge')).toBeDefined();
	});

	it('Fitzhì should not display the summary badges without a project loaded', () => {

		function field(id: string): HTMLInputElement {
			return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
		}

		referentialService.legendsLoaded$.next(true);
		projectService.projectLoaded$.next(false);
		fixture.detectChanges();

		expect(field('#sonarSummaryBadge')).toBeNull();
		expect(field('#staffSummaryBadge')).toBeNull();
		expect(field('#auditSummaryBadge')).toBeNull();

		projectService.project = new Project();
		projectService.projectLoaded$.next(true);
		expect(field('#sonarSummaryBadge')).toBeDefined();
		expect(field('#staffSummaryBadge')).toBeDefined();
		expect(field('#auditSummaryBadge')).toBeDefined();
	});

	it('The mean of Sonar evaluations is properly calculated.', () => {
		function field(id: string): HTMLInputElement {
			return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
		}

		referentialService.legendsLoaded$.next(true);
		const project = new Project();
		project.sonarProjects = [];

		const sp1 = new SonarProject();
		sp1.key = 'one';
		sp1.sonarEvaluation = new SonarEvaluation(50, 1000);
		project.sonarProjects.push(sp1);

		const sp2 = new SonarProject();
		sp2.key = 'two';
		sp2.sonarEvaluation = new SonarEvaluation(20, 3000);
		project.sonarProjects.push(sp2);

		projectService.project = project;
		projectService.projectLoaded$.next(true);
		fixture.detectChanges();
		expect(field('#sonarSummaryBadge')).toBeDefined();

		expect(projectService.calculateSonarEvaluation(projectService.project) === 28).toBeTruthy();
	});

	it('Displaying the medal.', () => {
	});

});


