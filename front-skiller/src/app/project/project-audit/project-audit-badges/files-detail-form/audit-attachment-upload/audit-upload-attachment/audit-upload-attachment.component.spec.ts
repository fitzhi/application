import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditUploadAttachmentComponent } from './audit-upload-attachment.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';

describe('AuditUploadAttachmentComponent', () => {
	let component: AuditUploadAttachmentComponent;
	let fixture: ComponentFixture<AuditUploadAttachmentComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [  ],
			imports: [ RootTestModule]

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
