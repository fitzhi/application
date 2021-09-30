import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { RiskLegend } from 'src/app/data/riskLegend';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { SkillService } from '../../skill/service/skill.service';
import { CinematicService } from '../cinematic.service';
import { ProjectService } from '../project/project.service';
import { ReferentialService } from '../referential.service';
import { DashboardService } from './dashboard.service';
import { dataRiskLegends } from './data-riskLegends';

describe('DashboardService.colorTile testing', () => {
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
		dashboardService = TestBed.inject(DashboardService);
		expect(dashboardService).toBeDefined();

		referentialService = TestBed.inject(ReferentialService);

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

	it('Execute the creation with a comparison with the legends loaded from the referential', () => {
		expect(component).toBeTruthy();
		expect(component.colorTiles[0]).toEqual(dataRiskLegends[0].color);
		expect(component.colorTiles[10]).toEqual(dataRiskLegends[10].color);
	});

	it ('Produce color of perfection', () => {
		expect(component).toBeTruthy();
		const color = dashboardService.colorTile(1, 1000000, referentialService.optimalStaffNumberPerMoOfCode[0]);
		expect(color).toEqual('#1CB745');
	});

	it ('Produce the worst color for the worst project', () => {
		expect(component).toBeTruthy();
		const color = dashboardService.colorTile(5, 1000000, 0);
		expect(color).toEqual('#8B0000');
	});

	it ('Produce different colors of risk depending on the skill minimum level', () => {
		expect(component).toBeTruthy();

		let color = dashboardService.colorTile(1, 1000000, referentialService.optimalStaffNumberPerMoOfCode[3]);
		expect(color).not.toEqual('#1CB745');

		color = dashboardService.colorTile(4, 1000000, referentialService.optimalStaffNumberPerMoOfCode[3]);
		expect(color).toEqual('#1CB745');
	});

	it('Execute a test if we exceed the perfection', () => {
		expect(component).toBeTruthy();
		component.color = dashboardService.colorTile(1, referentialService.optimalStaffNumberPerMoOfCode[0], 2);
		fixture.detectChanges();
		expect('#1CB745').toEqual(component.color);
	});

});
