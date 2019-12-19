import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditUploadAttachmentComponent } from './audit-upload-attachment.component';

describe('AuditUploadAttachmentComponent', () => {
	let component: AuditUploadAttachmentComponent;
	let fixture: ComponentFixture<AuditUploadAttachmentComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ AuditUploadAttachmentComponent ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(AuditUploadAttachmentComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
