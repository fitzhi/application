import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditBadgeComponent } from './audit-badge.component';

describe('AuditBadgeComponent', () => {
	let component: AuditBadgeComponent;
	let fixture: ComponentFixture<AuditBadgeComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ AuditBadgeComponent ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(AuditBadgeComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
