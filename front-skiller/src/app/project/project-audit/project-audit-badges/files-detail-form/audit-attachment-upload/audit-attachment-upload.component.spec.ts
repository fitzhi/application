import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditAttachmentUploadComponent } from './audit-attachment-upload.component';

describe('AuditAttachmentUploadComponent', () => {
	let component: AuditAttachmentUploadComponent;
	let fixture: ComponentFixture<AuditAttachmentUploadComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ AuditAttachmentUploadComponent ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(AuditAttachmentUploadComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
