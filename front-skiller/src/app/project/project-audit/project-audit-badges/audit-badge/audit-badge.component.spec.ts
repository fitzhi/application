import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditBadgeComponent } from './audit-badge.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';
import { MatGridListModule } from '@angular/material/grid-list';
import { AuditGraphicBadgeComponent } from './audit-graphic-badge/audit-graphic-badge.component';

describe('AuditBadgeComponent', () => {
	let component: AuditBadgeComponent;
	let fixture: ComponentFixture<AuditBadgeComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [AuditBadgeComponent, AuditGraphicBadgeComponent],
			imports : [RootTestModule, MatGridListModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(AuditBadgeComponent);
		component = fixture.componentInstance;
		component.index = 1;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
