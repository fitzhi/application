import { HttpClient, HttpEventType, HttpRequest, HttpResponse } from '@angular/common/http';
import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { traceOn } from 'src/app/global';
import { FileService } from 'src/app/service/file.service';
import { BaseDirective } from '../../../base/base-directive.directive';
import { Constants } from '../../../constants';
import { Collaborator } from '../../../data/collaborator';
import { DeclaredExperience } from '../../../data/declared-experience';
import { StaffResume } from '../../../data/StaffResume';
import { MessageBoxService } from '../../../interaction/message-box/service/message-box.service';
import { BackendSetupService } from '../../../service/backend-setup/backend-setup.service';

@Component({
	selector: 'app-staff-upload-cv',
	templateUrl: './staff-upload-cv.component.html',
	styleUrls: ['./staff-upload-cv.component.css'],
})
export class StaffUploadCvComponent extends BaseDirective implements OnInit, OnDestroy {

	/**
	 * Full path of the selected resume file.
	 */
	applicationFile: File;

	/**
	 * Current collaborator active in the Staff Form.
	 */
	staff: Collaborator;

	/**
	 * Declared experience retrieved from the resume of this collaborator.
	 */
	declaredExperience: DeclaredExperience[];

	/**
	 * Progression Bar representing the upload speed.
	 */
	progression = new Subject<number>();
	progress = this.progression.asObservable();

	constructor(
		private httpClient: HttpClient,
		private fileService: FileService,
		private messageBoxService: MessageBoxService,
		private dialogRef: MatDialogRef<StaffUploadCvComponent>,
		private backendSetupService: BackendSetupService,
		@Inject(MAT_DIALOG_DATA) public data: any) {
		super();
	}

	ngOnInit() {
		this.staff = <Collaborator>this.data;
	}

	submit() {
		if (typeof this.applicationFile === 'undefined') {
			this.messageBoxService.error('ERROR', 'You must select the application document first !');
		} else {
			if (this.fileService.checkApplicationFormat(this.applicationFile)) {
				this.upload(this.applicationFile);
			}
		}
	}

	public fileEvent($event) {
		this.applicationFile = $event.target.files[0];
		if (traceOn()) {
			console.log('Testing checkApplicationFormat for ' + this.applicationFile.type);
		}
		this.fileService.checkApplicationFormat(this.applicationFile);
	}

	/**
	 * Upload the file.
	 * @param file the application to upload
	 */
	upload(file: File) {

		if (traceOn()) {
			console.log('Uploading the file ' + this.applicationFile.name);
		}

		// create a new multipart-form for the file to upload.
		const formData: FormData = new FormData();
		formData.append('file', file, file.name);
		formData.append('type', <string><any>Constants.APPLICATION_FILE_TYPE_ALLOWED.get(this.applicationFile.type));

		// create a HTTP-post request and pass the form
		// tell it to report the upload progress
		const req = new HttpRequest('POST',
		this.backendSetupService.url() + '/staff/' + this.staff.idStaff + '/uploadCV', formData, {
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
					const response = <StaffResume>event.body;
					this.declaredExperience = response.experiences;
					if (traceOn()) {
						console.groupCollapsed(this.declaredExperience.length + ' experiences detected : ');
						response.experiences.forEach(element => console.log (element.title));
						console.groupEnd();
					}

					this.staff.application = this.fileService.extractFilename(file.name);
					this.staff.typeOfApplication = Constants.APPLICATION_FILE_TYPE_ALLOWED.get(this.applicationFile.type);

					// Close the progress-stream if we get an answer form the API
					// The upload is complete
					this.progression.complete();

					// We close this dialog and returns the skills detected on the application.
					this.dialogRef.close(response.experiences);
				}
			},
				responseInError => {
					if ( (responseInError.error) && (responseInError.error.message)) {
						this.messageBoxService.error('Uploading error !', responseInError.error.message);
					} else {
						this.messageBoxService.error('Uploading error !', 'An error occurs when uploading the application file');
					}
					this.dialogRef.close([]);
				}));
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}


}

