export class Library {

	/**
	 * Dependency pathname.
	 */
	exclusionDirectory: string;

	/**
	 * Type of dependency.
	 * 1 : Dependency detected by the crawler.
	 * 2 : Dependency declared for the project
	 */
	type: number;

	public constructor(exclusionDirectory: string, type: number) {
		this.exclusionDirectory = exclusionDirectory;
		this.type = type;
	}
}
