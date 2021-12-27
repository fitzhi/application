import { Injectable } from '@angular/core';
import { BehaviorSubject, EMPTY, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AttachmentFile } from 'src/app/data/AttachmentFile';
import { traceOn } from 'src/app/global';
import { ProjectService } from 'src/app/service/project/project.service';

@Injectable({
	providedIn: 'root'
})
export class AuditAttachmentService {

	/**
	 * Attachment files array for
	 */
	public attachmentFiles = new Map<number, AttachmentFile[]>();

	/**
	 * Attachment files array for
	 */
	public attachmentFiles$ = new BehaviorSubject<Map<number, AttachmentFile[]>>(new Map());

	constructor(private projectService: ProjectService) {

		// We keep active this subscription all along the life of the application.
		this.projectService.projectLoaded$
			.pipe(switchMap(
				doneAndOk => ((doneAndOk) ? of(projectService.project) : EMPTY)
			)).subscribe({
				next: project => this.loadAttachmentFiles()
			});
	}

	/**
	 * Load the map of Attachment files used to be displayed in the component files-detail-form.
	 */
	public loadAttachmentFiles() {
		if (traceOn()) {
			console.log ('loadAttachmentFiles()');
		}
		Object.keys(this.projectService.project.audit).forEach(key => {
			const idTopic = Number(key);
			this.attachmentFiles.set(
				idTopic,
				this.projectService.project.audit[key].attachmentList);
			this.addUploadtrailer(idTopic);
		});
		this.attachmentFiles$.next(this.attachmentFiles);
	}

	/**
	 * Inform the system of :
	 * - the creation of a new attachment file
	 * - the update of an existing one
	 *
	 * @param idTopic the topic in the audit scope
	 * @param attachmentFile the attachment file
	 */
	public updateAttachmentFile(idTopic: number, attachmentFile: AttachmentFile): void {
		// If no label has been provided for the file, we use the filename as label.
		if ((!attachmentFile.label) || (attachmentFile.label.length === 0)) {
			if (traceOn()) {
				console.log (`Setting the label to ${attachmentFile.fileName}`);
			}
			attachmentFile.label = attachmentFile.fileName;
		}
		this.attachmentFiles.get(idTopic)[attachmentFile.idFile] = attachmentFile;
		this.addUploadtrailer(idTopic);
		this.attachmentFiles$.next(this.attachmentFiles);
	}

	/**
	 * Get the next file identifier.
	 * @param idTopic the topic in the audit scope
	 * @returns the next file identifier for this topic
	 */
	nextAttachmentFile(idTopic: number): number {
		// We substract 1 because we have added an empty line in the table
		return this.attachmentFiles.get(idTopic).length - 1;
	}

	/**
	 * Inform the system that an attachment file has been removed.
	 * @param idFile the file attachment identifier to be removed
	 */
	public removeAttachmentFile(idTopic: number, idFile: number): void {
		// We remove a file from an array of 4 elements : ths array is complete.
		// So, after the deletion, we can once again add an upload trailer.
		if (this.attachmentFiles.get(idTopic).length === 4) {
			this.attachmentFiles.get(idTopic).splice(idFile, 1);
			this.addUploadtrailer(idTopic);
		} else {
			this.attachmentFiles.get(idTopic).splice(idFile, 1);
		}
		this.attachmentFiles$.next(this.attachmentFiles);
	}

	/**
	 * Add the upload trailer : an empty attachment which allows the end-user to upload a new File.
	 * @param idTopic Topic the topic identifier where the audit trailer should be added
	 */
	public addUploadtrailer(idTopic: number): void {

		const attachmentFiles = this.attachmentFiles.get(idTopic);
		const length = attachmentFiles.length;

		//
		// We add an upload trailer
		// - if we're beginning with an empty list of attachments
		// - If we've to add one if none exists yet.
		//
		if ( 	  (length === 0) ||
				( (length < 4) && (attachmentFiles[length - 1].fileName))
			) {
			if (traceOn()) {
				console.log ('After adding Upload trailer', attachmentFiles);
			}
			attachmentFiles.push(new AttachmentFile(attachmentFiles.length));
		}
	}
}
