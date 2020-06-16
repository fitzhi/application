import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PieChartComponent } from './pie-chart.component';
import { MatTableModule } from '@angular/material/table';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReferentialService } from 'src/app/service/referential.service';
import { RouterTestingModule } from '@angular/router/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { CinematicService } from 'src/app/service/cinematic.service';
import { Component, ViewEncapsulation } from '@angular/core';
import { PieDashboardService } from '../service/pie-dashboard.service';
import { TypeSlice } from '../type-slice';
import { ListSkillService } from 'src/app/skill/list-skill-service/list-skill.service';
import { SkillService } from 'src/app/service/skill.service';
import { Skill } from 'src/app/data/skill';
import { of, BehaviorSubject } from 'rxjs';
import { AuditChosenDetail } from 'src/app/project/project-audit/project-audit-badges/audit-badge/audit-chosen-detail';

describe('PieChartComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;


	@Component({
		selector: 'app-host-component',
		template: `<div style="width:400px;height:400px;background-color:whiteSmoke">
										<app-pie-chart
											[radius]=150
											[pie]=3
											[active]=true>
										</app-pie-chart>
								</div>`
	})
	class TestHostComponent {
	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ PieChartComponent, TestHostComponent ],
			imports: [MatTableModule, HttpClientTestingModule, RouterTestingModule, MatDialogModule],
			providers: [ReferentialService, CinematicService, PieDashboardService]
		})
		.compileComponents();

		const pieDashboardService = TestBed.inject(PieDashboardService);
		pieDashboardService.slices$.next(
			[
				{
					id: 0,
					type: TypeSlice.Sonar,
					angle: 45,
					color: 'green',
					offset: 0,
					activated: false,
					selected: false,
					projects: []
				},
				{
					id: 1,
					type: TypeSlice.Sonar,
					angle: 45,
					color: 'orange',
					offset: 45,
					activated: false,
					selected: false,
					projects: []
				},
				{
					id: 2,
					type: TypeSlice.Sonar,
					angle: 45,
					color: 'red',
					offset: 90,
					activated: false,
					selected: false,
					projects: []
				}
			]);
/*
			const listSkillService = TestBed.inject(ListSkillService);
			const skillService = TestBed.inject(SkillService);
			const skill = new Skill(1, 'First skill');
			const skills = [];
			skills.push(skill);
			const spyGetSkills = spyOn(listSkillService, 'getSkills').and.returnValue(skills);
			const getSkill = spyOn(listSkillService, 'getSkill$').and.returnValue(of(skill));
*/
		}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create & display the Pie chart', () => {

		expect(component).toBeTruthy();
	});
});
