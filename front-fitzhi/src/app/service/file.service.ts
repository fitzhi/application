import { Injectable } from '@angular/core';
import { Constants } from '../constants';
import { MessageBoxService } from '../interaction/message-box/service/message-box.service';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { saveAs } from 'file-saver';
import { traceOn } from '../global';

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

	/**
	 * Type of local OS.
	 */
	osGlobals = { isWin: false, isOsX: false, isNix: false };

	constructor(
		private messageBoxService: MessageBoxService,
		private http: HttpClient) {

		const appVer = navigator.appVersion;
		if (traceOn()) {
			console.log ('appVer', appVer);
		}
		if  (appVer.indexOf('Win') !== -1) {
			this.osGlobals.isWin = true;
		} else if (appVer.indexOf('Mac') !== -1) {
			this.osGlobals.isOsX = true;
			} else if (appVer.indexOf('X11') !== -1)   {
				this.osGlobals.isNix = true;
			} else if (appVer.indexOf('Linux') !== -1) {
				this.osGlobals.isNix = true;
		}
	}

	/**
	 * Returns `true` if the given file is of a valid type (DOC, DOCX or PDF)
	 * @param file the passed file
	 */
	checkApplicationFormat(file: File): boolean {
		if (file) {
			if (!Constants.APPLICATION_FILE_TYPE_ALLOWED.has(file.type)) {
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

	/**
	 * Download a file from the backend server.
	 * @param filename the filename to localy save.
	 * @param url the url in charge of the transmission of data.
	 */
	public downloadFile(filename: string, url: string) {
		const headers = new HttpHeaders();
		headers.set('Accept', 'application/msword');

		this.http.get(url, { headers: headers, responseType: 'blob' })
			.subscribe(data => {
				this.saveToFileSystem(data, filename, 'application/octet-stream');
			});
	}

	/**
     * Save the application file on the the file system.
	 * @param data content of the file
	 * @param filename name of the file to store on local
	 * @param typeOfFile type of file (DOC, DOCX, PDF...)
     */
	private saveToFileSystem(data, filename, typeOfFile) {
		const blob = new Blob([data], { type: typeOfFile });
		saveAs(blob, filename);
	}

	/**
	 * Extract and return the filename from the complete pathname
	 * @param pathname the complete pathname
	 */
	public extractFilename(pathname: string): string {
		const pathParts: string[] = pathname.split(this.getPathSeparator());
		return pathParts[pathParts.length - 1];
	}

	/**
	 * Return the local path separator depending the local OS (Windows '\', or Unix, OsX '/')
	 */
	private getPathSeparator() {

		if (this.osGlobals.isWin) {
			return '\\';
		}

		if (this.osGlobals.isOsX || this.osGlobals.isNix) {
			return '/';
		}

		// default to *nix system.
		return '/';
	}
}
