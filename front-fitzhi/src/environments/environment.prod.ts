import { RunTimeFile } from "./runtime-file";

/**
 * Check the file `environment.ts` for a complete presentation of these settings.
 */
 export const environment = {
	production: true,
	debug: false,
	version: require('../../package.json').version,
	apiUrl: 'http://localhost:8080',
	buildTime: RunTimeFile.buildtime,
	autoConnect: false
};
