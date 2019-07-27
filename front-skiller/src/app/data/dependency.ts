export class Dependency {

	/**
	 * Dependency pathname.
	 */
	pathname: string;

	/**
	 * Type of dependency.
	 * 1 : Dependency detected by the crawler.
	 * 2 : Dependency declared for the project
	 */
	type: number;

	public constructor(pathname: string, type: number) {
		this.pathname = pathname;
		this.type = type;
	}
}
