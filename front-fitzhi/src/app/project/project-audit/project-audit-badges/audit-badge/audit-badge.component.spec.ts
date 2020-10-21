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

describe('AuditBadgeComponent', () => {
	let component: AuditBadgeComponent;
	let fixture: ComponentFixture<AuditBadgeComponent>;

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

		fixture = TestBed.createComponent(AuditBadgeComponent);
		component = fixture.componentInstance;
		component.id = 1;
		component.evaluation = 50;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
