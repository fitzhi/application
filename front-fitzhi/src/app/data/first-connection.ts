
/**
 * This class is mainly used by the method `backendSetupService.isVeryFirstConnection(...)`
 */
export class FirstConnection {

	/**
	 * Public construction.
	 * @param connected `TRUE` if the connection succeeded, `FALSE` if it failed.
	 * @param first `TRUE` if this is the first initial connection, `FALSE` otherwise.
	 * @param validUrl the valid URL to be used if the server has returned a **302** response with a new location.
	 */
	constructor(
		public connected: boolean,
		public first?: boolean,
		public validUrl?: string) {}
}
