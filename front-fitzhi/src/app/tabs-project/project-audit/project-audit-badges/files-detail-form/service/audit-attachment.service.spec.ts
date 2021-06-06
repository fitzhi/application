import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { doesNotReject } from 'assert';
import { take } from 'rxjs/operators';
import { AttachmentFile } from 'src/app/data/AttachmentFile';
import { AuditTopic } from 'src/app/data/AuditTopic';
import { Project } from 'src/app/data/project';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { FileService } from 'src/app/service/file.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential.service';

import { AuditAttachmentService } from './audit-attachment.service';

describe('AuditAttachmentService', () => {

	let projectService: ProjectService;
	let auditAttachmentService: AuditAttachmentService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule, MatDialogModule],
			providers: [ProjectService, ReferentialService, CinematicService]
		}).compileComponents();
		projectService = TestBed.inject(ProjectService);
		auditAttachmentService = TestBed.inject(AuditAttachmentService);
	}));

	beforeEach(() => {
		projectService.project = new Project(1789, 'Revolutionary project');
		projectService.project.audit[0] = new AuditTopic(1789, 0, 50);
		projectService.project.audit[0].attachmentList.push(new AttachmentFile(0, 'filename.pdf', 0, 'my label'));
		projectService.project.audit[3] = new AuditTopic(1789, 3, 100);
		projectService.project.audit[3].attachmentList.push(new AttachmentFile(0, 'one.pdf', 1, 'my label One'));
		projectService.project.audit[3].attachmentList.push(new AttachmentFile(1, 'two.pdf', 1, 'my label Two'));
		projectService.project.audit[3].attachmentList.push(new AttachmentFile(2, 'three.pdf', 1, 'my label Three'));
		projectService.project.audit[3].attachmentList.push(new AttachmentFile(3, 'four.pdf', 1, 'my label Four'));
		projectService.projectLoaded$.next(true);

	});

	it('should be created.', done => {
		expect(auditAttachmentService).toBeTruthy();
		auditAttachmentService.attachmentFiles$.subscribe({
			next: attachmentFiles => {
				expect(auditAttachmentService.attachmentFiles.get(0)).toBeDefined();
				expect(auditAttachmentService.attachmentFiles.get(0).length).toBe(2);
				expect(auditAttachmentService.attachmentFiles.get(3)).toBeDefined();
				expect(auditAttachmentService.attachmentFiles.get(3).length).toBe(4);
				done();
			}
		});
	});

	it('should correctly handle a new attachmentFile.', done => {
		auditAttachmentService.attachmentFiles$.pipe(take(1)).subscribe({
			next: attachmentFiles => {
				auditAttachmentService.updateAttachmentFile( 0, new AttachmentFile(1, 'other_file.doc', 2, 'other label'));
			}
		}).add(function() {
			auditAttachmentService.attachmentFiles$.pipe(take(1)).subscribe({
				next: attachmentFiles => {
					expect(auditAttachmentService.attachmentFiles.get(0)).toBeDefined();
					expect(auditAttachmentService.attachmentFiles.get(0).length).toBe(3);
					expect(auditAttachmentService.attachmentFiles.get(0)[1].fileName).toBe('other_file.doc');
					expect(auditAttachmentService.attachmentFiles.get(0)[2].fileName).toBeNull();
					done();
				}
			});
		});
	});

	it('should correctly add uploadTrailer.', done => {
		auditAttachmentService.attachmentFiles$.pipe(take(1)).subscribe({
			next: attachmentFiles => {
				expect(auditAttachmentService.attachmentFiles.get(0)).toBeDefined();
				expect(auditAttachmentService.attachmentFiles.get(0).length).toBe(2);
				auditAttachmentService.attachmentFiles.get(0)[1].fileName = 'one';
				auditAttachmentService.addUploadtrailer(0);
				expect(auditAttachmentService.attachmentFiles.get(0).length).toBe(3);
				auditAttachmentService.attachmentFiles.get(0)[2].fileName = 'two';
				auditAttachmentService.addUploadtrailer(0);
				expect(auditAttachmentService.attachmentFiles.get(0).length).toBe(4);

				// We do not exceed 4 records by topic
				auditAttachmentService.attachmentFiles.get(0)[3].fileName = 'three';
				auditAttachmentService.addUploadtrailer(0);
				expect(auditAttachmentService.attachmentFiles.get(0).length).toBe(4);

				done();
			}
		});
	});

	it('should remove correctly an attachment.', done => {
		auditAttachmentService.attachmentFiles$.pipe(take(1)).subscribe({
			next: attachmentFiles => {
				expect(auditAttachmentService.attachmentFiles.get(3)).toBeDefined();
				expect(auditAttachmentService.attachmentFiles.get(3).length).toBe(4);

				auditAttachmentService.removeAttachmentFile(3, 2);
				expect(auditAttachmentService.attachmentFiles.get(3)[2].fileName).toBe('four.pdf');
				expect(auditAttachmentService.attachmentFiles.get(3).length).toBe(4);

				auditAttachmentService.removeAttachmentFile(3, 0);
				expect(auditAttachmentService.attachmentFiles.get(3)[0].fileName).toBe('two.pdf');
				expect(auditAttachmentService.attachmentFiles.get(3).length).toBe(3);

				auditAttachmentService.removeAttachmentFile(3, 0);
				expect(auditAttachmentService.attachmentFiles.get(3).length).toBe(2);
				done();
			}
		});
	});
});

