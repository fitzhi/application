import { environment } from '../environments/environment';

/**
 * Return `true` if the application is in debuging mode & allow tracing its flow & stats in the console.
 */
export function traceOn(): boolean {

	//
	// If the console has not been activated, we do not need to write into the console
	// This test is not available on all browsers.
	//
	if (!console || !console.log) {
		return false;
	}
	return environment.debug;
}

export const HttpCodes = {
	success : 200,
	notFound : 404,
	created: 201,
	noContent: 204,
	methodNotAllowed: 405
};


