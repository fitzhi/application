import { Measure } from './measure';

/**
 * This class represents the number of files detected on the repository per language.
 */
export class LanguageFilesCount {
	/**
	 * Language of a source file detected.
	 */
	language: string;

	/**
	 * Number of files of this language detected.
	 */
	count: number;
}
