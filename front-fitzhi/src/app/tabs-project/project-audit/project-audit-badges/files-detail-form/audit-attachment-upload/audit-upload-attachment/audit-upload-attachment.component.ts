import { Component, OnInit, Inject, OnDestroy } from '@angular/core';
import { FileService } from 'src/app/service/file.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { AuditAttachmentComponent, AuditAttachment } from '../audit-attachment.component';
import { BaseComponent } from 'src/app/base/base.component';
import { Constants } from 'src/app/constants';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { HttpRequest, HttpClient, HttpEventType, HttpResponse } from '@angular/common/http';
import { Subject } from 'rxjs';
import { traceOn } from 'src/app/global';

@Component({
	selector: 'app-audit-upload-attachment',
	templateUrl: './audit-upload-attachment.component.html',
	styleUrls: ['./audit-upload-attachment.component.css']
})
export class AuditUploadAttachmentComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * Full path of the selected audit attachment file.
	 */
	attachmentFile: File;

	/**
	 * This object is passed when opening the diagBox.
	 */
	attachment: AuditAttachment;

	/**
	 * Progression Bar representing the upload speed.
	 */
	progression = new Subject<number>();
	progress = this.progression.asObservable();

	/**
	 * this `boolean` is setting that this file is valid for uploading.
	 */
	public validFile = false;

	constructor(
		private fileService: FileService,
		private dialogRef: MatDialogRef<AuditAttachmentComponent>,
		private backendSetupService: BackendSetupService,
		private httpClient: HttpClient,
		private messageBoxService: MessageBoxService,
		@Inject(MAT_DIALOG_DATA) public data: any) {
			super();
		}

	/**
	 * Initialization of the component.
	 */
	ngOnInit() {
		this.attachment = <AuditAttachment>this.data;
	}

	/**
	 * This method is invoked when the user has chosen a file.
	 * @param $event event received
	 */
	public fileEvent($event) {
		this.attachmentFile = $event.target.files[0];
		if (traceOn()) {
			console.log('Testing checkAttachmentFormat for ' + this.attachmentFile.type);
		}
		this.validFile = this.fileService.checkApplicationFormat(this.attachmentFile);
		console.log (this.classFilename());
	}

	/**
	 * End-user asks for the upload.
	 */
	public submit(): void {
		this.upload(this.attachmentFile);
	}

	/**
	 * Upload the file.
	 * @param file the application to upload
	 */
	upload(file: File) {

		if (traceOn()) {
			console.log('Uploading the file ' + this.attachmentFile.name);
		}

		// create a new multipart-form for the file to upload.
		const formData: FormData = new FormData();
		formData.append('file', file, file.name);
		formData.append('label', <string><any>this.attachment.label);
		formData.append('type', <string><any>Constants.APPLICATION_FILE_TYPE_ALLOWED.get(this.attachmentFile.type));

		// create a HTTP-post request and pass the form
		// tell it to report the upload progress
		const req = new HttpRequest('POST',
		this.backendSetupService.url() 
			+ '/project/' + this.attachment.idProject 
			+ '/audit/' +  this.attachment.idTopic + '/attachment', formData, {
			reportProgress: true
		});

		// send the HTTP-request and subscribe for progress-updates
		this.subscriptions.add(
			this.httpClient.request(req).subscribe(event => {
				if (event.type === HttpEventType.UploadProgress) {
					// calculate the progress percentage
					const percentDone = Math.round(100 * event.loaded / event.total);
					// pass the percentage into the progress-stream
					this.progression.next(percentDone);
				} else if (event instanceof HttpResponse) {
					const doneAndOk = <boolean>event.body;
					if (doneAndOk) {
						if (traceOn()) {
							console.log ('Upload done for file ' + this.attachmentFile.name + ' of type ' + this.attachmentFile.type);
						}
						this.attachment.filename = this.fileService.extractFilename(file.name);
						this.attachment.type = Constants.APPLICATION_FILE_TYPE_ALLOWED.get(this.attachmentFile.type);
					}
					// Close the progress-stream if we get an answer form the API
					// The upload is complete
					this.progression.complete();

					// We close this dialog and returns the skills detected on the application.
					this.dialogRef.close(this.attachment);
				}
			},
				responseInError => {
					this.messageBoxService.error('Uploading error !', responseInError);
					// If the upload failed, we return null.
					this.dialogRef.close(null);
				}));
	}

	/**
	 * Return the classname formating the paragraph displaying the filename.
	 * - green : the file is valid for uploading (type and size)
	 * - red : the file is invalid for upload
	 */
	public classFilename(): string {
		return (this.validFile) ? 'filenameSuccess' : 'filenameError';
	}

	/**
	* Calling the base class to revoke all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}


}
