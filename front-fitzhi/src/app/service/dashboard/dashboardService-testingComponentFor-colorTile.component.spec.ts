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
					'</table>'
	})
	class TestHostComponent {
		public colorTiles = [];
		public dataRiskLegends: RiskLegend[];
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
			const color = dashboardService.colorTile(10000, i);
			component.colorTiles.push(color);
		}
		component.dataRiskLegends = dataRiskLegends;
		fixture.detectChanges();

		expect(component.colorTiles[0]).toEqual(dataRiskLegends[0].color);
		expect(component.colorTiles[10]).toEqual(dataRiskLegends[10].color);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
