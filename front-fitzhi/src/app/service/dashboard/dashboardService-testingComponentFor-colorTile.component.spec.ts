import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, Input } from '@angular/core';
import {DashboardService} from './dashboard.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { ReferentialService } from '../referential.service';
import { SkillService } from '../skill.service';
import { StaffListComponent } from 'src/app/tabs-staff-list/staff-list/staff-list.component';
import { StaffListService } from 'src/app/staff-list-service/staff-list.service';
import { ProjectService } from '../project.service';
import {dataRiskLegends} from './data-riskLegends';
import { RiskLegend } from 'src/app/data/riskLegend';

describe('DashboardService.colorTile testing', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let dashboardService: DashboardService;

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

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [TestHostComponent],
			imports: [HttpClientTestingModule, MatDialogModule],
			providers: [DashboardService, ReferentialService, SkillService, StaffListService, ProjectService]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		dashboardService = TestBed.get(DashboardService);
		expect(dashboardService).toBeDefined();

		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;

		for (let i = 10; i >= 0; i--) {
			const color = dashboardService.colorTile(10000000, i * 3);
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

	it('Execute a test if we exceed the perfection', () => {
		expect(component).toBeTruthy();
		component.color = dashboardService.colorTile(DashboardService.OPTIMAL_NUMBER_OF_STAFF_PER_1000_K_OF_CODE, 2);
		fixture.detectChanges();
		expect('#1CB745').toEqual(component.color);
	});

});
