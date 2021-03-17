import { RunTimeFile } from "./runtime-file";

export const environment = {
	production: true,
	debug: false,
	version: require('../../package.json').version,
	apiUrl: 'https://spoq.fitzhi.com',
	buildTime: RunTimeFile.buildtime
};
