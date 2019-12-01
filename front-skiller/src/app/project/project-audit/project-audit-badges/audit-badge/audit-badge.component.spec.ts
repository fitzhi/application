import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditBadgeComponent } from './audit-badge.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';
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

describe('AuditBadgeComponent', () => {
	let component: AuditBadgeComponent;
	let fixture: ComponentFixture<AuditBadgeComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [AuditBadgeComponent, AuditGraphicBadgeComponent],
			imports : [RootTestModule, MatGridListModule, MatFormFieldModule, MatSliderModule
				, MatInputModule, FormsModule ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		const cinematicService: CinematicService = TestBed.get(CinematicService);
		cinematicService.auditHistory[1] = new AuditDetailsHistory();
		const referentialService: ReferentialService = TestBed.get(ReferentialService);
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
