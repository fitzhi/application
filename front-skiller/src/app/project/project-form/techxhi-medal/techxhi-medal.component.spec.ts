import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TechxhiMedalComponent } from './techxhi-medal.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';
import { ProjectService } from 'src/app/service/project.service';
import { Project } from 'src/app/data/project';
import { BehaviorSubject } from 'rxjs';
import { ReferentialService } from 'src/app/service/referential.service';
import { PortalHostDirective } from '@angular/cdk/portal';
import { SonarProject } from 'src/app/data/SonarProject';
import { SonarEvaluation } from 'src/app/data/sonar-evaluation';
import { RiskLegend } from 'src/app/data/riskLegend';

describe('TechxhiMedalComponent', () => {
	let component: TechxhiMedalComponent;
	let fixture: ComponentFixture<TechxhiMedalComponent>;
	let referentialService: ReferentialService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ],
			imports: [RootTestModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TechxhiMedalComponent);
		component = fixture.componentInstance;
		referentialService = TestBed.get(ReferentialService);

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


		component.project$ = new BehaviorSubject<Project>(new Project());
		component.colorOfRisk = 'none';
		fixture.detectChanges();
	});

	it('Techxhì should not display the Sonar summary badge without color legends loaded', () => {
		expect(component).toBeTruthy();

		function field(id: string): HTMLInputElement {
			return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
		}
		expect(field('#sonarSummaryBadge')).toBeNull();

		referentialService.legendsLoaded$.next(true);

		expect(field('#sonarSummaryBadge')).toBeDefined();

	});

	it('Techxhì should not display the Sonnar summary badge without any associated Sonar project', () => {
		function field(id: string): HTMLInputElement {
			return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
		}

		referentialService.legendsLoaded$.next(true);
		const p1 = new Project();
		component.project$.next(p1);
		fixture.detectChanges();
		expect(field('#sonarSummaryBadge')).toBeNull();

		p1.sonarProjects = [];
		expect(field('#sonarSummaryBadge')).toBeNull();

		p1.sonarProjects.push(new SonarProject());
		expect(field('#sonarSummaryBadge')).toBeDefined();
	});

	it('Techxhì should not display the summary badges without a project loaded', () => {

		function field(id: string): HTMLInputElement {
			return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
		}

		referentialService.legendsLoaded$.next(true);
		component.project$.next(null);
		fixture.detectChanges();

		expect(field('#sonarSummaryBadge')).toBeNull();
		expect(field('#staffSummaryBadge')).toBeNull();
		expect(field('#auditSummaryBadge')).toBeNull();

		component.project$.next(new Project());
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

		component.project$.next(project);
		fixture.detectChanges();
		expect(field('#sonarSummaryBadge')).toBeDefined();

		expect(component.globalSonarEvaluation === 28).toBeTruthy();
	});

});
