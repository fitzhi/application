import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditBadgeComponent } from './audit-badge.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { AuditGraphicBadgeComponent } from './audit-graphic-badge/audit-graphic-badge.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSliderModule } from '@angular/material/slider';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { CinematicService } from 'src/app/service/cinematic.service';
import { AuditDetailsHistory } from 'src/app/service/cinematic/audit-details-history';
import { ReferentialService } from 'src/app/service/referential.service';
import { RiskLegend } from 'src/app/data/riskLegend';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Component } from '@angular/core';

describe('AuditBadgeComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: `<div style="width: 400px; height: 400px; margin: 20px; background-color: lightYellow">
						
						<app-audit-badge
							[id]="id"				
							[evaluation]="evaluation"	
							[weight]="weight"
							[title]="title">
						</app-audit-badge>

					</div>`
	})
	class TestHostComponent {
		id = 1;
		evaluation = 0;
		weight = 100;
		title = 'the Title';
	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [AuditBadgeComponent, AuditGraphicBadgeComponent],
			providers: [ReferentialService, CinematicService],
			imports : [MatGridListModule, MatFormFieldModule, MatSliderModule,
				MatInputModule, FormsModule, HttpClientTestingModule, MatDialogModule,
				BrowserAnimationsModule ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		const cinematicService: CinematicService = TestBed.inject(CinematicService);
		cinematicService.auditHistory[1] = new AuditDetailsHistory();
		const referentialService: ReferentialService = TestBed.inject(ReferentialService);
		const risk = new RiskLegend();
		risk.level = 5;
		risk.color = 'blue';
		referentialService.legends.push (risk);
		referentialService.referentialLoaded$.next(true);
		
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create correctly the audit-badge Component', () => {
		expect(component).toBeTruthy();
	});
});
