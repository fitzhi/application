export class AttachmentFile {

	/**
	 * Public constuction.
	 * @param idFile the identfier file within the topic (from 1 to 4)
 	 * @param fileName the name of the file.
 	 * @param typeOfFile the type of the file (e.g. Word, Pdf...).
	 * @param label the label associated to this file
	 */
	constructor(
		public idFile: number,
		public fileName: string = null,
		public typeOfFile?: number,
		public label?: string) {}

}
