import { RunTimeFile } from "./runtime-file";

export const environment = {
	production: true,
	debug: false,
	version: require('../../package.json').version,
	apiUrl: 'http://localhost:8080',
	buildTime: RunTimeFile.buildtime
};
