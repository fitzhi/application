import { environment } from '../environments/environment';

/**
 * Return `true` if the application is in debuging mode & allow tracing its flow & stats in the console.
 */
export function traceOn(): boolean {
	return environment.debug;
}
