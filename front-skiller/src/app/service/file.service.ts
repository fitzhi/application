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

	/**
	 * Location of images inside Techchì.
	 */
	private IMAGES_DIR = './assets/img/';

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

	/**
	 * Return the logo associated to the type of application.
	 * The logo returned will be displayed in a `download` button.
	 * @param typeOfApplication the type of application (DOC, DOCX, PDF...)
	 */
	getAssociatedIcon(typeOfApplication: number) {
		switch (typeOfApplication) {
			case Constants.FILE_TYPE_DOC:
			case Constants.FILE_TYPE_DOCX:
				return this.IMAGES_DIR + 'word.png';
			case Constants.FILE_TYPE_PDF:
				return this.IMAGES_DIR + 'pdf.png';
			default:
				console.error('Shoud not pass here : unknown type of application' + typeOfApplication);
				return 'none';
		}
	}

	/**
	 * Return the logo associated to the type of application.
	 * The logo returned will be displayed in a `download` button.
	 * @param typeOfApplication the type of application (DOC, DOCX, PDF...)
	 */
	getAssociatedAwesomeFont(typeOfApplication: number) {
		switch (typeOfApplication) {
			case Constants.FILE_TYPE_DOC:
			case Constants.FILE_TYPE_DOCX:
				return 'fas fa-file-word';
			case Constants.FILE_TYPE_PDF:
				return 'fas fa-file-pdf';
			default:
				console.error('Shoud not pass here : unknown type of application' + typeOfApplication);
				return 'fas fa-exclamation';
		}
	}

}
