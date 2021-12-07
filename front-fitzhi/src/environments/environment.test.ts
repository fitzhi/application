import { RunTimeFile } from './runtime-file';

/**
 * This file can be replaced during build by using the `fileReplacements` array.
 * ng build ---prod` replaces `environment.ts` with `environment.prod.ts`.
 * The list of file replacements can be found in `angular.json`.
 *
 * - production : production mode `TRUE`/`FALSE`
 * - debug : display into the console the log messages. _It is possible to be in production in debug mode_
 * - version: the version imported from the `package.json` file. This version is logged in the console at startup.
 * - buildTime : date/time of build for this release. This timestamp is logged in the console at startup.
 * - apiUrl: the URL where the backend server is supposed to be deployed. This URL is used when starting for the first time from a browser,
 * and saved into the local storage.
 * - autoConnect : automatic connection mode `TRUE`/`FALSE`.
 * This setting is actually only `TRUE` for the demo version of Fitzhi in the website.
 */
export const environment = {
	production: false,
	debug: true,
	version: require('../../package.json').version,
	buildTime: RunTimeFile.buildtime,
	apiUrl: 'URL_OF_SERVER',
	autoConnect: false
};

/*
 * In development mode, to ignore zone related error stack frames such as
 * `zone.run`, `zoneDelegate.invokeTask` for easier debugging, you can
 * import the following file, but please comment it out in production mode
 * because it will have performance impact when throw error
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
