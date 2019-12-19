import { Injectable } from '@angular/core';
import { Constants } from '../constants';
import { MessageBoxService } from '../message-box/service/message-box.service';

/**
 * This class is providing upload or download services.
 */
@Injectable({
	providedIn: 'root'
})
export class FileService {

	constructor(private messageBoxService: MessageBoxService) {
	}

	/**
	 * Returns `true` if the given file is of a valid type (DOC, DOCX or PDF)
	 * @param applicationFile the passed file
	 */
	checkApplicationFormat(applicationFile: File): boolean {
		if (applicationFile) {
			if (!Constants.APPLICATION_FILE_TYPE_ALLOWED.has(applicationFile.type)) {
				this.messageBoxService.error('ERROR', 'Only the formats .DOC, .DOCS and .PDF are supported !');
				return false;
			} else {
				return true;
			}
		}
	}
}
