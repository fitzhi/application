import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { RiskLegend } from 'src/app/data/riskLegend';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { SkillService } from '../../skill/service/skill.service';
import { CinematicService } from '../cinematic.service';
import { ProjectService } from '../project/project.service';
import { ReferentialService } from '../referential/referential.service';
import { DashboardColor } from './dashboard-color';
import { DashboardService } from './dashboard.service';
import { dataRiskLegends } from './data-riskLegends';

describe('DashboardService.colorTile(...)', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let dashboardService: DashboardService;
	let referentialService: ReferentialService;

	@Component({
		selector: 'app-host-component',
		template: 	'<table style="width:100px; height:550px;background-color:whiteSmoke">' +
					'<tr>' +
					'<td>' +
						'<div *ngFor="let color of colorTiles; index as i" ' +
							'style="width: 50px; height:50px;" [style.background-color]=color></div>' +
					'</td>' +
					'<td>' +
						'<div *ngFor="let legend of dataRiskLegends" ' +
							'style="width: 50px; height:50px;" [style.background-color]=legend.color></div>' +
					'</td>' +
					'</tr>' +
					'</table><br/>' +
					'<table><tr><td>The processed color if we exceed the perfection : </td>' +
					'<td><div style="width: 50px; height:50px" [style.background-color]=color></div></td></tr>'
	})
	class TestHostComponent {
		public colorTiles = [];
		public dataRiskLegends: RiskLegend[];
		public color = 'blue';
	}

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [TestHostComponent],
			imports: [HttpClientTestingModule, MatDialogModule],
			providers: [DashboardService, ReferentialService, SkillService, StaffListService, ProjectService, CinematicService]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		referentialService = TestBed.inject(ReferentialService);
		console.log (referentialService.optimalStaffNumberPerMoOfCode);
		referentialService.optimalStaffNumberPerMoOfCode.push(8);
		referentialService.optimalStaffNumberPerMoOfCode.push(4);
		referentialService.optimalStaffNumberPerMoOfCode.push(2);
		referentialService.optimalStaffNumberPerMoOfCode.push(1);
		referentialService.optimalStaffNumberPerMoOfCode.push(1);


		dashboardService = TestBed.inject(DashboardService);
		expect(dashboardService).toBeDefined();

		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;

		for (let i = 10; i >= 0; i--) {
			const color = dashboardService.colorTile(
					1,
					10000000,
					i * referentialService.optimalStaffNumberPerMoOfCode[0]);
			component.colorTiles.push(color);
		}
		component.dataRiskLegends = dataRiskLegends;
		fixture.detectChanges();

	});

	it('should execute the creation with a comparison with the legends loaded from the referential', () => {
		expect(component).toBeTruthy();
		expect(component.colorTiles[0]).toEqual(dataRiskLegends[0].color);
		expect(component.colorTiles[10]).toEqual(dataRiskLegends[10].color);
	});

	it ('should be able to produced the color of perfection', () => {
		expect(component).toBeTruthy();
		// 	--color-selected: #28a745;
		const color = dashboardService.colorTile(1, 1000000, referentialService.optimalStaffNumberPerMoOfCode[0]);
		expect(color).toEqual('#28A745');
	});

	it ('should produce the worst color for the worst project', () => {
		expect(component).toBeTruthy();
		const color = dashboardService.colorTile(5, 1000000, 0);
		expect(color).toEqual('#8B0000');
	});

	it ('should produce different colors of risk depending on the skill minimum level', () => {
		expect(component).toBeTruthy();

		// We are not at the perfection level with the input.
		let color = dashboardService.colorTile(1, 1000000, referentialService.optimalStaffNumberPerMoOfCode[3]);
		expect(color).not.toEqual('#28A745');

		// We reach the perfection level with the input.
		color = dashboardService.colorTile(4, 1000000, referentialService.optimalStaffNumberPerMoOfCode[3]);
		expect(color).toEqual('#28A745');
	});

	it('should execute a test if we exceed the perfection', () => {
		expect(component).toBeTruthy();
		component.color = dashboardService.colorTile(1, referentialService.optimalStaffNumberPerMoOfCode[0], 2);
		fixture.detectChanges();
		expect('#28A745').toEqual(component.color);
	});

});
