import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditGraphicBadgeComponent } from './audit-graphic-badge.component';

describe('AuditGraphicBadgeComponent', () => {
	let component: AuditGraphicBadgeComponent;
	let fixture: ComponentFixture<AuditGraphicBadgeComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ AuditGraphicBadgeComponent ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(AuditGraphicBadgeComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
