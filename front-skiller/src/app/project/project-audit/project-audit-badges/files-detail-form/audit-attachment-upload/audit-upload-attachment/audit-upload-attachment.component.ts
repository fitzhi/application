import { Component, OnInit, Inject } from '@angular/core';
import { FileService } from 'src/app/service/file.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { AuditAttachmentComponent } from '../audit-attachment.component';

@Component({
	selector: 'app-audit-upload-attachment',
	templateUrl: './audit-upload-attachment.component.html',
	styleUrls: ['./audit-upload-attachment.component.css']
})
export class AuditUploadAttachmentComponent implements OnInit {

	/**
	 * Full path of the selected audit attachment file.
	 */
	applicationFile: File;


	constructor(
		private fileService: FileService,
		private dialogRef: MatDialogRef<AuditAttachmentComponent>,
		private backendSetupService: BackendSetupService,
		@Inject(MAT_DIALOG_DATA) public data: any) {}

	ngOnInit() {
	}

}
