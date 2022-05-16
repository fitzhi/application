import { RunTimeFile } from "./runtime-file";

/**
 * Check the file `environment.ts` for a complete presentation of these settings.
 */
 export const environment = {
	production: true,
	debug: false,
	version: require('../../package.json').version,
	apiUrl: 'https://spoq.fitzhi.com',
	buildTime: RunTimeFile.buildtime,
	autoConnect: true,
	test: false,
};
