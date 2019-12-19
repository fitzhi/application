import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditAttachmentComponent } from './audit-attachment.component';

describe('AuditAttachmentComponent', () => {
	let component: AuditAttachmentComponent;
	let fixture: ComponentFixture<AuditAttachmentComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ AuditAttachmentComponent ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(AuditAttachmentComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
