import { Injectable } from '@angular/core';
import { AttachmentFile } from 'src/app/data/AttachmentFile';
import { BehaviorSubject } from 'rxjs';
import { Constants } from 'src/app/constants';
import { traceOn } from 'src/app/global';

@Injectable({
	providedIn: 'root'
})
export class AuditAttachmentService {

	/**
	 * Attachment files array.
	 */
	public _attachmentFiles: AttachmentFile[] = [];

	/**
	 * This observable emits the array of attachments available for the current audit topic.
	 */
	public attachmentFile$  = new  BehaviorSubject<AttachmentFile[]>([]);

	constructor() { }

	/**
	 * Inform the system of the active attachment files array.
	 * @param attachmentFiles the array of attachment files
	 */
	public emitAttachmentFiles(attachmentFiles: AttachmentFile[]): void {
		if (traceOn()) {
			console.log ('settings current %d files', attachmentFiles.length);
		}
		this._attachmentFiles = attachmentFiles;
		this.addUploadtrailer();
		this.attachmentFile$.next(this._attachmentFiles);
	}

	/**
	 * Inform the system of :
	 * - the creation of a new attachment file
	 * - the update of an existing one
	 * @param attachmentFile the attachment file
	 */
	public emitAddUpdAttachmentFile(attachmentFile: AttachmentFile): void {
		// If no label has been provided for the file, we use the filename as label.
		if ((!attachmentFile.label) || (attachmentFile.label.length === 0)){
			if (traceOn()) {
				console.log ('Setting the label to %s', attachmentFile.fileName);
			}
			attachmentFile.label = attachmentFile.fileName;
		}
		this._attachmentFiles[attachmentFile.idFile] = attachmentFile;
		this.addUploadtrailer();
		this.attachmentFile$.next(this._attachmentFiles);
	}

	/**
	 * Inform the system that an attachment file has been removed.
	 * @param idFile the file attachment identifier to be removed
	 */
	public emitRemoveAttachmentFile(idFile: number): void {
		if (!this._attachmentFiles[idFile]) {
			throw new Error ('Should not pass here : a record is expected here ' + idFile);
		}
		this._attachmentFiles.splice(idFile, 1);
		this.renumberingId();
		this.addUploadtrailer();
		this.attachmentFile$.next(this._attachmentFiles);
	}

	/**
	 * renumber the ids of the array after the deletion of an attachment file.
	 */
	private renumberingId(): void {
		let id = 0;
		this._attachmentFiles.forEach((attachmentFile: AttachmentFile) => {
			attachmentFile.idFile = id++;
		});
	}

	/**
	 * Add the upload trailer : an empty attachment which permits the end-user to upload a new File.
	 */
	private addUploadtrailer(): void {
		const length = this._attachmentFiles.length;
		//
		// We add an upload trailer
		// - if we're beginning with an empty list of attachments
		// - If we've to add one if none exists yet.
		//
		if ( 	  (length === 0) ||
				( (length < 4) && (this._attachmentFiles[length - 1].fileName))
			) {
			if (traceOn()) {
				console.log ('After adding Upload trailer', this._attachmentFiles);
			}
			this._attachmentFiles.push(new AttachmentFile(this._attachmentFiles.length));
		}
	}
}
