/**
 * This class represents the number of files detected on the repository per language.
 */
export class FilesStats {

	/**
	 * @param langage the language counted (Java, TS...)
	 * @param numberOfFiles Number of files detected by Sonar.
	 */
	constructor (
		public language: string,
		public numberOfFiles: number) {}
}
