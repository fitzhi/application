import { TestBed } from '@angular/core/testing';

import { AuditAttachmentService } from './audit-attachment.service';

describe('AuditAttachmentService', () => {
	beforeEach(() => TestBed.configureTestingModule({}));

	it('should be created', () => {
		const service: AuditAttachmentService = TestBed.inject(AuditAttachmentService);
		expect(service).toBeTruthy();
	});
});
